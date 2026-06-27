package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.ClassType;

public class ClassSymbol extends TypeSymbol {
    public final ClassType valueType;

    public ClassSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column);
        this.valueType = new ClassType(this);
    }

    @Override
    public String toString() {
        return String.format("<Class %s>", name);
    }
}
