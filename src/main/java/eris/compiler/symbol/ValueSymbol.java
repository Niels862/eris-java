package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.InferenceType;
import eris.compiler.type.Type;

public abstract class ValueSymbol extends Symbol {
    public Type type;

    public ValueSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column, false);
        this.type = new InferenceType(this);
    }

    public void setType(Type type) {
        System.out.printf("%s -> %s%n", this.type, type);
        this.type = type;
    }
}
