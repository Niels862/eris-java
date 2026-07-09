package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.ClassType;
import eris.compiler.type.FunctionType;

import java.util.Collections;
import java.util.List;

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
        FunctionType type = new FunctionType(Collections.singletonList(valueType), valueType);
        VariableSymbol thisParameter = new VariableSymbol("this", module, line, column, valueType);
        symbol.finalize(type, Collections.singletonList(thisParameter));
        return symbol;
    }
}
