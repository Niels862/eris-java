package eris.compiler.ast;

import eris.compiler.Token;
import eris.compiler.type.InferenceType;
import eris.compiler.type.Type;

public abstract class ExpressionNode extends Node {
    private Type inferredType;

    ExpressionNode(Token token) {
        super(token);
    }

    public Type getInferredType() {
        assert inferredType != null;
        return inferredType;
    }

    public void setInferredType(Type inferredType) {
        assert this.inferredType == null;
        assert !(inferredType instanceof InferenceType);
        this.inferredType = inferredType;
    }

    public boolean hasInferredType() {
        return inferredType != null;
    }
}
