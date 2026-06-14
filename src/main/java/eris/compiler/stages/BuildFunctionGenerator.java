package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.*;
import eris.compiler.symbol.Symbol;

import java.util.Queue;

public class BuildFunctionGenerator extends NodeVisitor<Void> {
    private final BuildModule module;
    private final Node node;

    private final Queue<Node> taskQueue;
    private final StatementGenerator generator = new StatementGenerator();
    private Symbol symbol;

    public BuildFunctionGenerator(BuildModule module, Node node, Queue<Node> taskQueue) {
        this.module = module;
        this.node = node;
        this.taskQueue = taskQueue;
    }

    public BuildFunction generate() throws CompilerError {
        node.accept(this);
        assert symbol != null;
        return new BuildFunction(node, symbol);
    }

    @Override
    public Void visit(ModuleNode node) throws CompilerError {
        for (FunctionNode functionNode : node.functions) {
            functionNode.accept(generator);
        }

        symbol = node.entrySymbol;
        return null;
    }

    @Override
    public Void visit(FunctionNode node) throws CompilerError {
        for (StatementNode statement : node.statements) {
            statement.accept(generator);
        }

        symbol = node.symbol;
        return null;
    }

    private class StatementGenerator extends NodeVisitor<Void> {
        @Override
        public Void visit(FunctionNode node) {
            taskQueue.add(node);
            return null;
        }

        @Override
        public Void visit(ReturnStatementNode node) {
            return null;
        }

        @Override
        public Void visit(IntegerNode node) {
            return null;
        }
    }
}
