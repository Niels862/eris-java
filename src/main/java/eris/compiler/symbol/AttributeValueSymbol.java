package eris.compiler.symbol;

import eris.compiler.BuildModule;

public class AttributeValueSymbol extends ValueSymbol {
    public final ClassSymbol clazz;

    public AttributeValueSymbol(String name, BuildModule module, int line, int column, ClassSymbol clazz) {
        super(name, module, line, column);
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return String.format("<AttributeValueSymbol %s::%s : %s>", clazz.name, name, type);
    }
}
