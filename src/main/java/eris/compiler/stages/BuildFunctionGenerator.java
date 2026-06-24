package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.*;
import eris.compiler.ir.*;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.Symbol;
import eris.compiler.symbol.SymbolTable;
import eris.module.constant.Constant;
import eris.module.constant.FunctionReferenceConstant;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BuildFunctionGenerator extends NodeVisitor<Void> {
    private final BuildModule module;

    private final Queue<Task> taskQueue = new ArrayDeque<>();
    private final StatementGenerator generator = new StatementGenerator();
    private Symbol symbol;
    private final ScopeHandler scopeHandler = new ScopeHandler();

    private IntermediateBlock block;

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
        block = new IntermediateBlock(0);
        if (task.enclosing != null) {
            scopeHandler.enterScope(task.enclosing);
        }
        task.node.accept(this);
        assert symbol != null;
        if (task.enclosing != null) {
            scopeHandler.leaveScope(task.enclosing);
        }
        return new BuildFunction(task.node, symbol, block);
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
            functionNode.accept(generator);
        }

        scopeHandler.leaveScope(node.globalScope);
        symbol = node.entrySymbol;
        return null;
    }

    @Override
    public Void visit(FunctionNode node) throws CompilerError {
        scopeHandler.enterScope(node.scope);

        for (StatementNode statement : node.statements) {
            statement.accept(generator);
        }

        scopeHandler.leaveScope(node.scope);
        symbol = node.symbol;
        return null;
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
        public Void visit(ReturnStatementNode node) throws CompilerError {
            if (node.value != null) {
                node.value.accept(this);
            } else {
                throw new UnsupportedOperationException();
            }
            emit(new Return());
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
                argument.accept(this);
            }
            emit(new Call(functionSymbol));
        }

        private void emitIndirectFunctionCall(ExpressionNode function, List<ExpressionNode> arguments) throws CompilerError {
            throw new UnsupportedOperationException();
        }

        @Override
        public Void visit(IntegerNode node) {
            emit(new LoadConstant(node.value));
            return null;
        }

        private void addTask(Node node) {
            taskQueue.add(new Task(node, scopeHandler.getSymbolTable()));
        }
    }

    private record Task(
            Node node,
            SymbolTable enclosing
    ) {}
}
