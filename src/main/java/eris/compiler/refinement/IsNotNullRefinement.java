package eris.compiler.refinement;

import eris.compiler.type.NullableType;
import eris.compiler.type.Type;

public class IsNotNullRefinement extends Refinement {
    @Override
    public Refinement narrowingUnion(Refinement other) {
        if (other instanceof IsNotNullRefinement) {
            return this;
        }

        return null;
    }

    @Override
    public Type apply(Type type) {
        if (type instanceof NullableType nullableType) {
            return nullableType.type;
        } else {
            return type;
        }
    }

    @Override
    public String toString() {
        return "IsNotNull";
    }
}
