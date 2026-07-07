package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.ir.*;
import eris.compiler.symbol.*;

import java.util.*;

public class BuildFunctionGenerator extends NodeVisitor<Void> {
    private final BuildModule module;

    private final Queue<Task> taskQueue = new ArrayDeque<>();
    private FunctionSymbol symbol;
    private final ScopeHandler scopeHandler = new ScopeHandler();
    private final TypeContext context = TypeContext.instance;

    private final StatementGenerator statementGenerator = new StatementGenerator();
    private final ExpressionGenerator expressionGenerator = new ExpressionGenerator();
    private final AssignmentTargetGenerator assignmentTargetGenerator = new AssignmentTargetGenerator();

    private List<BasicBlock> blocks;
    private BasicBlock block;
    private List<VariableSymbol> locals;
    private List<VariableSymbol> parameters;
    private int nextBlockId;

    private Node currentNode;

    public BuildFunctionGenerator(BuildModule module) {
        this.module = module;
    }

    public List<BuildFunction> generate(ModuleNode moduleNode) throws CompilerError {
        List<BuildFunction> functions = new ArrayList<>();

        taskQueue.add(new Task(moduleNode, null));
        while (!taskQueue.isEmpty()) {
            Task task = taskQueue.remove();
            BuildFunction function = buildTask(task);
            functions.add(function);
        }

        return functions;
    }

    private BuildFunction buildTask(Task task) throws CompilerError {
        buildTaskPrologue(task);
        task.node.accept(this);
        buildTaskEpilogue(task);
        return new BuildFunction(task.node, symbol, blocks, parameters, locals);
    }

    private void buildTaskPrologue(Task task) throws CompilerError {
        symbol = null;
        nextBlockId = 0;
        blocks = new ArrayList<>();
        block = new BasicBlock(nextBlockId++);
        locals = new ArrayList<>();
        parameters = new ArrayList<>();
        blocks.add(block);

        if (task.node instanceof FunctionNode functionNode) {
            for (ParameterNode parameter : functionNode.parameters) {
                parameter.symbol.setDeclared();
                parameters.add(parameter.symbol);
            }
        }

        if (task.enclosing != null) {
            scopeHandler.enterScope(task.enclosing);
        }
    }

    private void buildTaskEpilogue(Task task) throws CompilerError {
        addBlock(makeBlock());
        emit(new Fallthrough());

        assert symbol != null;
        if (task.enclosing != null) {
            scopeHandler.leaveScope(task.enclosing);
        }
    }

    @Override
    public Void visit(ModuleNode node) throws CompilerError {
        symbol = node.entrySymbol;
        scopeHandler.enterScope(node.globalScope);

        Symbol mainSymbol = scopeHandler.getSymbolTable().lookup("main");
        if (mainSymbol instanceof FunctionSymbol mainFunctionSymbol) {
            emit(new Call(mainFunctionSymbol));
            emit(new Halt());
        } else {
            throw new CompilerError(module, "Module does not have a main function");
        }

        for (FunctionNode functionNode : node.functions) {
            functionNode.accept(statementGenerator);
        }

        scopeHandler.leaveScope(node.globalScope);
        return null;
    }

    @Override
    public Void visit(FunctionNode node) throws CompilerError {
        symbol = node.symbol;
        scopeHandler.enterScope(node.scope);

        for (StatementNode statement : node.statements) {
            statementGenerator.generate(statement);
        }

        scopeHandler.leaveScope(node.scope);
        return null;
    }

    private void addTask(Node node) {
        taskQueue.add(new Task(node, scopeHandler.getSymbolTable()));
    }

    public VariableSymbol lookupVariableSymbol(Node node, String name) throws CompilerError {
        Symbol symbol = scopeHandler.getSymbolTable().lookup(name);

        System.out.println(symbol + " " + name + " " + scopeHandler.getSymbolTable());

        if (symbol instanceof VariableSymbol variableSymbol) {
            if (!variableSymbol.isDeclared()) {
                throw new CompilerError(
                        module, node.line, node.column,
                        String.format("Variable %s is referenced before declaration", name));
            }
            return variableSymbol;
        } else if (symbol == null) {
            throw new CompilerError(module, node.line, node.column, name + " is not declared in this scope");
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public <T extends IntermediateInstruction> T emit(T instruction) {
        if (block.getLast() instanceof TerminatorInstruction) {
            addBlock(makeBlock());
        }

        block.instructions.add(instruction);
        if (currentNode != null) {
            instruction.setPosition(currentNode.line, currentNode.column);
        }

        return instruction;
    }

    public void addBlock(BasicBlock block) {
        if (!(this.block.getLast() instanceof TerminatorInstruction)) {
            emit(new Jump(block));
        }

        this.block = block;
        blocks.add(block);
    }

    public BasicBlock makeBlock() {
        return new BasicBlock(nextBlockId++);
    }

    private class Generator extends NodeVisitor<Void> {
        public void generate(Node node) throws CompilerError {
            Node previousNode = currentNode;
            currentNode = node;
            node.accept(this);
            currentNode = previousNode;
        }
    }

    private class StatementGenerator extends Generator {
        @Override
        public Void visit(FunctionNode node) {
            addTask(node);
            return null;
        }

        @Override
        public Void visit(VariableNode node) throws CompilerError {
            if (node.initialValue != null) {
                expressionGenerator.generate(node.initialValue);
                Convert converter = emit(new Convert(node.symbol.staticType));
                emit(new StoreLocal(node.symbol, true, converter));
            }
            node.symbol.setDeclared();
            locals.add(node.symbol);
            return null;
        }

        @Override
        public Void visit(AssignmentStatementNode node) throws CompilerError {
            expressionGenerator.generate(node.value);
            Convert converter = emit(new Convert(null));
            assignmentTargetGenerator.withConverter(converter).generate(node.target);
            return null;
        }

        @Override
        public Void visit(IfElseStatementNode node) throws CompilerError {
            scopeHandler.enterScope(node.thenScope);
            expressionGenerator.generate(node.condition);
            emit(new Convert(context.BOOL));

            BasicBlock thenBlock = makeBlock();
            BasicBlock elseBlock = makeBlock();
            BasicBlock exitBlock = makeBlock();

            emit(new Branch(thenBlock, elseBlock));
            addBlock(thenBlock);
            for (StatementNode statement : node.thenBody) {
                statementGenerator.generate(statement);
            }
            emit(new Jump(exitBlock));

            scopeHandler.leaveScope(node.thenScope);
            scopeHandler.enterScope(node.elseScope);

            addBlock(elseBlock);
            for (StatementNode statement : node.elseBody) {
                statementGenerator.generate(statement);
            }
            addBlock(exitBlock);

            scopeHandler.leaveScope(node.elseScope);
            return null;
        }

        @Override
        public Void visit(WhileStatementNode node) throws CompilerError {
            emitConditionalLoop(node.scope, node.condition, node.body, false);
            return null;
        }

        @Override
        public Void visit(DoWhileStatementNode node) throws CompilerError {
            emitConditionalLoop(node.scope, node.condition, node.body, true);
            return null;
        }

        @Override
        public Void visit(LoopStatementNode node) throws CompilerError {
            emitConditionalLoop(node.scope, null, node.body, false);
            return null;
        }

        private void emitConditionalLoop(
                SymbolTable scope,
                ExpressionNode condition,
                List<StatementNode> body,
                boolean isDoWhile) throws CompilerError {
            scopeHandler.enterScope(scope);

            BasicBlock loopBlock = makeBlock();
            BasicBlock nextBlock = makeBlock();
            BasicBlock exitBlock = makeBlock();

            if (!isDoWhile) {
                emit(new Jump(nextBlock));
            }

            addBlock(loopBlock);
            for (StatementNode statement : body) {
                statementGenerator.generate(statement);
            }

            addBlock(nextBlock);
            if (condition != null) {
                expressionGenerator.generate(condition);
                emit(new Convert(context.BOOL));
                emit(new Branch(loopBlock, exitBlock));
            } else {
                emit(new Jump(loopBlock));
            }

            addBlock(exitBlock);

            scopeHandler.leaveScope(scope);
        }

        @Override
        public Void visit(ExpressionStatementNode node) throws CompilerError {
            expressionGenerator.generate(node.expression);
            emit(new Pop());
            return null;
        }

        @Override
        public Void visit(ReturnStatementNode node) throws CompilerError {
            if (node.value != null) {
                expressionGenerator.generate(node.value);
                emit(new Convert(symbol.type.returnType));
            } else {
                throw new UnsupportedOperationException();
            }
            emit(new Return());
            return null;
        }
    }

    private class ExpressionGenerator extends Generator {
        @Override
        public Void visit(BinaryOperationNode node) throws CompilerError {
            expressionGenerator.generate(node.left);
            expressionGenerator.generate(node.right);
            emit(new BinaryOperation(node.operator));
            return null;
        }

        @Override
        public Void visit(CallNode node) throws CompilerError {
            if (node.function instanceof IdentifierNode identifier) {
                Symbol symbol = scopeHandler.getSymbolTable().lookup(identifier.name);
                if (symbol instanceof FunctionSymbol functionSymbol) {
                    emitFunctionCall(functionSymbol, identifier, node.arguments);
                    return null;
                }
            }

            emitIndirectFunctionCall(node.function, node.arguments);
            return null;
        }

        private void emitFunctionCall(
                FunctionSymbol functionSymbol,
                IdentifierNode function,
                List<ExpressionNode> arguments) throws CompilerError {
            for (ExpressionNode argument : arguments) {
                expressionGenerator.generate(argument);
            }
            emit(new Call(functionSymbol));
        }

        private void emitIndirectFunctionCall(ExpressionNode function, List<ExpressionNode> arguments) throws CompilerError {
            throw new UnsupportedOperationException();
        }

        @Override
        public Void visit(IdentifierNode node) throws CompilerError {
            VariableSymbol symbol = lookupVariableSymbol(node, node.name);
            emit(new LoadLocal(symbol));
            return null;
        }

        @Override
        public Void visit(IntegerLiteralNode node) {
            emit(new LoadConstant(node.value));
            return null;
        }

        @Override
        public Void visit(BooleanLiteralNode node) throws CompilerError {
            emit(new LoadConstant(node.value));
            return null;
        }

        @Override
        public Void visit(StringLiteralNode node) throws CompilerError {
            emit(new LoadConstant(node.value));
            return null;
        }

        @Override
        public Void visit(NullLiteralNode node) throws CompilerError {
            emit(new LoadNull());
            return null;
        }
    }

    private class AssignmentTargetGenerator extends Generator {
        private Convert converter;

        AssignmentTargetGenerator withConverter(Convert converter) {
            this.converter = converter;
            return this;
        }

        @Override
        public Void defaultHandler(Node node) throws CompilerError {
            throw new CompilerError(module, node.line, node.column, "Invalid assignment target");
        }

        @Override
        public Void visit(IdentifierNode node) throws CompilerError {
            VariableSymbol symbol = lookupVariableSymbol(node, node.name);
            emit(new StoreLocal(symbol, false, converter));
            converter.toType = symbol.staticType;
            return null;
        }
    }

    private record Task(
            Node node,
            SymbolTable enclosing
    ) {}
}
