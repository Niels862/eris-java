package eris.compiler.refinement;

import eris.compiler.TypeContext;
import eris.compiler.type.Type;

public class IsNullRefinement extends Refinement {
    @Override
    public Refinement narrowUnion(Refinement other) {
        if (other instanceof IsNullRefinement) {
            return this;
        }

        return null;
    }

    @Override
    public Type apply(Type type) {
        return TypeContext.instance.NULL;
    }

    @Override
    public String toString() {
        return "IsNull";
    }
}
