package eris.compiler.symbol;

import eris.compiler.BuildModule;

public abstract class Symbol {
    public final String name;
    public final BuildModule module;
    public final int line;
    public final int column;

    public Symbol(String name, BuildModule module, int line, int column) {
        this.name = name;
        this.module = module;
        this.line = line;
        this.column = column;
    }

    public abstract String toString();
}
