package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.*;
import eris.compiler.ir.*;
import eris.compiler.symbol.Symbol;
import eris.module.constant.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BuildFunctionGenerator extends NodeVisitor<Void> {
    private final BuildModule module;
    private final ConstantManager constants;
    private final Node node;

    private final Queue<Node> taskQueue;
    private final StatementGenerator generator = new StatementGenerator();
    private Symbol symbol;

    private final IntermediateBlock block = new IntermediateBlock(0);

    public BuildFunctionGenerator(BuildModule module, ConstantManager constants, Node node, Queue<Node> taskQueue) {
        this.module = module;
        this.constants = constants;
        this.node = node;
        this.taskQueue = taskQueue;
    }

    public BuildFunction generate() throws CompilerError {
        node.accept(this);
        assert symbol != null;
        return new BuildFunction(node, symbol, block);
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
        public void emit(IntermediateInstruction instruction) {
            block.instructions.add(instruction);
        }

        @Override
        public Void visit(FunctionNode node) {
            taskQueue.add(node);
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
        public Void visit(IntegerNode node) {
            Constant constant = constants.getIntegerConstant(node.value);
            emit(new LoadConstant(constant));
            return null;
        }
    }
}
