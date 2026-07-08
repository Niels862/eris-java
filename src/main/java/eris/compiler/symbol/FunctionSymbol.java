package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.FunctionType;

public class FunctionSymbol extends Symbol {
    public FunctionType type;

    public FunctionSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column);
    }

    public void finalize(FunctionType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("<Function %s : %s>", name, type);
    }
}
