package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.ClassType;
import eris.compiler.type.ClassValueType;
import eris.compiler.type.FunctionType;
import eris.compiler.type.Type;

import java.util.ArrayList;
import java.util.List;

public class ClassSymbol extends TypeSymbol {
    public final ClassValueType valueType;
    public final ClassType classType;

    public List<ValueSymbol> attributes;
    public SymbolTable symbolTable;
    public FunctionSymbol constructor;

    public ClassSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column, true);
        this.valueType = new ClassValueType(this);
        this.classType = new ClassType(this);
    }

    public void setMeta(List<ValueSymbol> attributes, SymbolTable symbolTable) {
        this.attributes = attributes;
        this.symbolTable = symbolTable;
        this.constructor = makeDefaultConstructor();
    }

    @Override
    public String toString() {
        return String.format("<Class %s>", name);
    }

    private FunctionSymbol makeDefaultConstructor() {
        List<ValueSymbol> parameters = new ArrayList<>();
        parameters.add(new LocalValueSymbol("this", module, line, column));

        List<Type> parameterTypes = new ArrayList<>();
        for (ValueSymbol attribute : attributes) {
            parameters.add(attribute);
            parameterTypes.add(attribute.type);
        }

        FunctionSymbol symbol = new FunctionSymbol(name + ".$constructor", module, line, column);
        symbol.setMeta(new FunctionType(parameterTypes, valueType), parameters);
        return symbol;
    }
}
