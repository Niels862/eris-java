package eris.compiler.type;

import eris.compiler.symbol.ClassSymbol;

public class ClassType extends Type {
    public final ClassSymbol symbol;

    public ClassType(ClassSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol.name;
    }
}
