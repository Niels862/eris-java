package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;

public abstract class Symbol {
    public final String name;
    protected final BuildModule module;
    protected final int line;
    protected final int column;

    private boolean active;

    public Symbol(String name, BuildModule module, int line, int column, boolean active) {
        this.name = name;
        this.module = module;
        this.line = line;
        this.column = column;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public void setActive() {
        active = true;
    }
}
