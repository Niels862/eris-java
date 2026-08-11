package eris.compiler.stages;

import eris.compiler.symbol.ClassSymbol;
import eris.compiler.symbol.ValueSymbol;
import eris.module.Attribute;
import eris.module.Class;

import java.util.ArrayList;
import java.util.List;

public class ClassCompiler {
    public Class compile(ClassSymbol symbol) {
        List<Attribute> attributes = new ArrayList<>();

        for (ValueSymbol attribute : symbol.attributes) {
            attributes.add(new Attribute(attribute.name, attribute.type.toTypeTag()));
        }

        return new Class(symbol.name, attributes);
    }
}
