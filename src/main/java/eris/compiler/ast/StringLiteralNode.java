package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

public class StringLiteralNode extends ExpressionNode {
    public final String value;

    public StringLiteralNode(Token token, String value) {
        super(token);
        this.value = value;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
