package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.*;
import eris.compiler.ir.*;
import eris.compiler.symbol.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BuildFunctionGenerator extends NodeVisitor<Void> {
    private final BuildModule module;

    private final Queue<Task> taskQueue = new ArrayDeque<>();
    private final StatementGenerator statementGenerator = new StatementGenerator();
    private final ExpressionGenerator expressionGenerator = new ExpressionGenerator();
    private Symbol symbol;
    private final ScopeHandler scopeHandler = new ScopeHandler();

    private IntermediateBlock block;
    private List<VariableSymbol> locals;
    private List<VariableSymbol> parameters;

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
        return new BuildFunction(task.node, symbol, block, parameters, locals);
    }

    private void buildTaskPrologue(Task task) throws CompilerError {
        block = new IntermediateBlock(0);
        locals = new ArrayList<>();
        parameters = new ArrayList<>();

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
        assert symbol != null;

        if (task.enclosing != null) {
            scopeHandler.leaveScope(task.enclosing);
        }
    }

    @Override
    public Void visit(ModuleNode node) throws CompilerError {
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
        symbol = node.entrySymbol;
        return null;
    }

    @Override
    public Void visit(FunctionNode node) throws CompilerError {
        scopeHandler.enterScope(node.scope);

        for (StatementNode statement : node.statements) {
            statement.accept(statementGenerator);
        }

        scopeHandler.leaveScope(node.scope);
        symbol = node.symbol;
        return null;
    }

    private void addTask(Node node) {
        taskQueue.add(new Task(node, scopeHandler.getSymbolTable()));
    }

    public void emit(IntermediateInstruction instruction) {
        block.instructions.add(instruction);
    }

    private class StatementGenerator extends NodeVisitor<Void> {
        @Override
        public Void visit(FunctionNode node) {
            addTask(node);
            return null;
        }

        @Override
        public Void visit(VariableNode node) throws CompilerError {
            if (node.initialValue != null) {
                node.initialValue.accept(expressionGenerator);
                emit(new StoreLocal(node.symbol));
            }
            node.symbol.setDeclared();
            locals.add(node.symbol);
            return null;
        }

        @Override
        public Void visit(ReturnStatementNode node) throws CompilerError {
            if (node.value != null) {
                node.value.accept(expressionGenerator);
            } else {
                throw new UnsupportedOperationException();
            }
            emit(new Return());
            return null;
        }
    }

    private class ExpressionGenerator extends NodeVisitor<Void> {
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
                argument.accept(this);
            }
            emit(new Call(functionSymbol));
        }

        private void emitIndirectFunctionCall(ExpressionNode function, List<ExpressionNode> arguments) throws CompilerError {
            throw new UnsupportedOperationException();
        }

        @Override
        public Void visit(IdentifierNode node) throws CompilerError {
            Symbol symbol = scopeHandler.getSymbolTable().lookup(node.name);
            if (symbol instanceof VariableSymbol variableSymbol) {
                if (!variableSymbol.isDeclared()) {
                    throw new CompilerError(
                            module, node.line, node.column,
                            String.format("Variable %s is referenced before declaration", node.name));
                }
                emit(new LoadLocal(variableSymbol));
            } else if (symbol == null) {
                throw new CompilerError(module, node.line, node.column, node.name + " is not declared in this scope");
            } else {
                throw new UnsupportedOperationException();
            }
            return null;
        }

        @Override
        public Void visit(IntegerNode node) {
            emit(new LoadConstant(node.value));
            return null;
        }
    }

    private record Task(
            Node node,
            SymbolTable enclosing
    ) {}
}
