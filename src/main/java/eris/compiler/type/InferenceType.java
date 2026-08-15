package eris.compiler.type;

import eris.compiler.symbol.ValueSymbol;

public class InferenceType extends Type {
    private static int nextId = 1;

    public final ValueSymbol symbol;
    public final int id;

    public InferenceType(ValueSymbol symbol) {
        this.symbol = symbol;
        this.id = nextId++;
    }

    @Override
    public String toString() {
        return "@t" + id;
    }
}
