package eris.compiler.symbol;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public Symbol lookup(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol == null && parent != null) {
            return parent.lookup(name);
        } else {
            return symbol;
        }
    }

    public boolean defines(String name) {
        return symbols.containsKey(name);
    }

    public void insert(String name, Symbol symbol) {
        assert !defines(name);
        assert !(lookup(name) instanceof TypeSymbol);
        symbols.put(name, symbol);
    }

    public String toString() {
        return symbols.toString();
    }
}
