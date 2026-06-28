package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

public class BinaryOperationNode extends ExpressionNode {
    public final String operator;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public BinaryOperationNode(Token token, String operator, ExpressionNode left, ExpressionNode right) {
        super(token);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
