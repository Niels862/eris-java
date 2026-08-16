package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.Type;

public class LocalValueSymbol extends ValueSymbol {
    public LocalValueSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column);
    }

    @Override
    public String toString() {
        return String.format("<LocalValueSymbol %s : %s>", name, type);
    }
}
