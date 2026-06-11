package eris.compiler.ast;

import eris.compiler.NodeVisitor;
import eris.compiler.Token;

public class IntegerNode extends ExpressionNode {
    public final int value;

    public IntegerNode(Token token, int value) {
        super(token);
        this.value = value;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
