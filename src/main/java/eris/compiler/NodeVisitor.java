package eris.compiler;

import eris.compiler.ast.*;

public abstract class NodeVisitor<T> {
    public T defaultHandler(AbstractNode node) {
        return null;
    }

    public T visit(ModuleNode node) {
        return defaultHandler(node);
    }

    public T visit(FunctionNode node) {
        return defaultHandler(node);
    }

    public T visit(ReturnStatementNode node) {
        return defaultHandler(node);
    }

    public T visit(IntegerNode node) {
        return defaultHandler(node);
    }
}
