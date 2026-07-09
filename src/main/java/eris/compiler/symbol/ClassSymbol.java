package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.ClassType;
import eris.compiler.type.FunctionType;

import java.util.Collections;

public class ClassSymbol extends TypeSymbol {
    public final ClassType valueType;
    public final FunctionSymbol constructor;

    public ClassSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column);
        this.valueType = new ClassType(this);
        this.constructor = makeDefaultConstructor();
    }

    @Override
    public String toString() {
        return String.format("<Class %s>", name);
    }

    private FunctionSymbol makeDefaultConstructor() {
        FunctionSymbol symbol = new FunctionSymbol(name + ".$constructor", module, line, column);
        symbol.finalize(new FunctionType(Collections.emptyList(), valueType));
        return symbol;
    }
}
