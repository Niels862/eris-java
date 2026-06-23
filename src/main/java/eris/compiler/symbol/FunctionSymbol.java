package eris.compiler.symbol;

import eris.compiler.BuildModule;

public class FunctionSymbol extends Symbol {
    public FunctionSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column);
    }

    @Override
    public String toString() {
        return String.format("<Function %s>", name);
    }
}
