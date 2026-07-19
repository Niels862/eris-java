package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.Type;

public class ValueSymbol extends Symbol {
    public Type type;
    public Scope scope;

    public ValueSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column, false);
    }

    public void setMeta(Type type, Scope scope) {
        this.type = type;
        this.scope = scope;
        setActive();
    }

    @Override
    public String toString() {
        return String.format("<Value(%s) %s : %s>", scope, name, type);
    }

    public enum Scope {
        Global,
        Local,
        Member
    }
}
