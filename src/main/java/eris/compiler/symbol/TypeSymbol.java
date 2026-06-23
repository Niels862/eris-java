package eris.compiler.symbol;

import eris.compiler.BuildModule;

public abstract class TypeSymbol extends Symbol {
    public TypeSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column);
    }
}
