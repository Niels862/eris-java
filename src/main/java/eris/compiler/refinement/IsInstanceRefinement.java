package eris.compiler.refinement;

import eris.compiler.type.Type;

public class IsInstanceRefinement extends Refinement {
    public final Type type;

    public IsInstanceRefinement(Type type) {
        this.type = type;
    }

    @Override
    public Refinement narrowingUnion(Refinement other) {
        return null;
    }

    @Override
    public Type apply(Type type) {
        return this.type;
    }

    @Override
    public String toString() {
        return "IsInstance(" + type + ")";
    }
}
