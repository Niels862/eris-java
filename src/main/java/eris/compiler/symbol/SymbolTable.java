package eris.compiler.symbol;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final SymbolTable parent;

    public SymbolTable() {
        this.parent = null;
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable(Symbol[] entries) {
        this.parent = null;
        for (Symbol entry : entries) {
            symbols.put(entry.name, entry);
        }
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

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public String toString() {
        return symbols.toString();
    }
}
