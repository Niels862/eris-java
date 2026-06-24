package eris.compiler.ast;

import eris.compiler.CompilerError;

public abstract class NodeVisitor<T> {
    public T defaultHandler(Node node) throws CompilerError {
        String string = String.format("%s does not implement %s", getClass().getSimpleName(), node);
        throw new UnsupportedOperationException(string);
    }

    public T visit(ModuleNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(FunctionNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(ReturnStatementNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(CallNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(IdentifierNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(IntegerNode node) throws CompilerError {
        return defaultHandler(node);
    }
}
