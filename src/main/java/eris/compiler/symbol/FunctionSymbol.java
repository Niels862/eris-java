package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.FunctionType;

import java.util.List;

public class FunctionSymbol extends Symbol {
    public FunctionType type;
    public List<VariableSymbol> parameters;

    public FunctionSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column);
    }

    public void finalize(FunctionType type, List<VariableSymbol> parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return String.format("<Function %s : %s>", name, type);
    }
}
