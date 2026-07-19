package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.ClassValueType;
import eris.compiler.type.FunctionType;
import eris.compiler.type.Type;

import java.util.ArrayList;
import java.util.List;

public class ClassSymbol extends TypeSymbol {
    public final ClassValueType valueType;
    public List<VariableSymbol> attributes;
    public FunctionSymbol constructor;

    public ClassSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column, true);
        this.valueType = new ClassValueType(this);
    }

    public void setMeta(List<VariableSymbol> attributes) {
        this.attributes = attributes;
        this.constructor = makeDefaultConstructor();
    }

    @Override
    public String toString() {
        return String.format("<Class %s>", name);
    }

    private FunctionSymbol makeDefaultConstructor() {
        List<VariableSymbol> parameters = new ArrayList<>();
        parameters.add(new VariableSymbol("this", module, line, column));

        List<Type> parameterTypes = new ArrayList<>();
        for (VariableSymbol attribute : attributes) {
            parameters.add(attribute);
            parameterTypes.add(attribute.type);
        }

        FunctionSymbol symbol = new FunctionSymbol(name + ".$constructor", module, line, column);
        symbol.setMeta(new FunctionType(parameterTypes, valueType), parameters);
        return symbol;
    }
}
