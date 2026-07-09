package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;

public abstract class Symbol {
    public final String name;
    protected final BuildModule module;
    protected final int line;
    protected final int column;

    public Symbol(String name, BuildModule module, int line, int column) {
        this.name = name;
        this.module = module;
        this.line = line;
        this.column = column;
    }

    public BuildModule getModule() {
        assert module != null;
        return module;
    }

    public abstract String toString();

    public CompilerError error(String message) {
        if (module != null) {
            return new CompilerError(module, line, column, message);
        } else {
            return new CompilerError(message);
        }
    }
}
