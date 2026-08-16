package eris.compiler.refinement;

import eris.compiler.type.Type;

public abstract class Refinement {
    abstract public Refinement narrowUnion(Refinement other);

    abstract public Type apply(Type type);

    abstract public String toString();
}

