package eris.compiler;

import eris.compiler.symbol.ClassSymbol;
import eris.compiler.symbol.Symbol;
import eris.compiler.symbol.SymbolTable;
import eris.compiler.type.ClassType;

public class TypeContext {
    public final ClassSymbol INT_SYMBOL = makeNativeClassSymbol("int");
    public final ClassType INT = INT_SYMBOL.valueType;

    public final SymbolTable symbolTable = new SymbolTable(new Symbol[]{
            INT_SYMBOL
    });

    public static final TypeContext instance = new TypeContext();

    private TypeContext() {}

    private ClassSymbol makeNativeClassSymbol(String name) {
        return new ClassSymbol(name, null, 0, 0);
    }
}
