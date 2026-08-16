package eris.compiler.type;

import eris.compiler.ast.ExpressionNode;

public class InferenceType extends Type {
    public final ExpressionNode expression;

    public InferenceType(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "@[" + expression + "]";
    }
}
