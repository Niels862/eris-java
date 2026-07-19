package eris.compiler.type;

import eris.compiler.symbol.ClassSymbol;

public class ClassValueType extends ValueType {
    public final ClassSymbol symbol;

    public ClassValueType(ClassSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol.name;
    }
}
