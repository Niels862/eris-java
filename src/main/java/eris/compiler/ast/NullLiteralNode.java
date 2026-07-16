package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

public class NullLiteralNode extends ExpressionNode {
    public NullLiteralNode(Token token) {
        super(token);
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {}
}
