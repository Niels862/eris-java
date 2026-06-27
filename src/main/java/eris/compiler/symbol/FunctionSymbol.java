package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.FunctionType;

public class FunctionSymbol extends Symbol {
    public final FunctionType type;

    public FunctionSymbol(String name, BuildModule module, int line, int column, FunctionType type) {
        super(name, module, line, column);
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("<Function %s : %s>", name, type);
    }
}
