package eris.compiler;

import eris.compiler.type.Type;

public class Refinement {
    public final Type type;

    public Refinement(Type type) {
        this.type = type;
    }

    public String toString() {
        return "<Refinement to "  + type + ">";
    }
}
