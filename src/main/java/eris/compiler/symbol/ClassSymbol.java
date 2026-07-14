package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.ast.VariableNode;
import eris.compiler.type.ClassType;
import eris.compiler.type.FunctionType;
import eris.compiler.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassSymbol extends TypeSymbol {
    public final ClassType valueType;
    public List<VariableSymbol> attributes;
    public FunctionSymbol constructor;

    public ClassSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column);
        this.valueType = new ClassType(this);
    }

    public void finalize(List<VariableSymbol> attributes) {
        this.attributes = Collections.unmodifiableList(attributes);
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
        parameterTypes.add(valueType);

        for (VariableSymbol attribute : attributes) {
            parameters.add(attribute);
            parameterTypes.add(attribute.getType()); // FIXME: enforce static type is set
        }

        FunctionSymbol symbol = new FunctionSymbol(name + ".$constructor", module, line, column);
        symbol.finalize(new FunctionType(parameterTypes, valueType), parameters);
        return symbol;
    }
}
