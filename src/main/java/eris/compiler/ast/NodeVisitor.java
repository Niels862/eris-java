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

    public T visit(ParameterNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(VariableNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(AssignmentStatementNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(IfElseStatementNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(ExpressionStatementNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(ReturnStatementNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(BinaryOperationNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(CallNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(IdentifierNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(IntegerLiteralNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(BooleanLiteralNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(StringLiteralNode node) throws CompilerError {
        return defaultHandler(node);
    }

    public T visit(NamedTypeNode node) throws CompilerError {
        return defaultHandler(node);
    }
}
