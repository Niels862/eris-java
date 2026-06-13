package eris.compiler.symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ScopeHandler {
    private final Stack<SymbolTable> enteredScopes = new Stack<>();

    public SymbolTable enterNewScope() {
        SymbolTable parent = getSymbolTable();
        SymbolTable symbolTable = new SymbolTable(parent);
        enteredScopes.push(symbolTable);
        return symbolTable;
    }

    public void leaveScope(SymbolTable scope) {
        SymbolTable top = enteredScopes.pop();
        assert top == scope;
    }

    public SymbolTable getSymbolTable() {
        if (enteredScopes.isEmpty()) {
            return null;
        } else {
            return enteredScopes.peek();
        }
    }
}
