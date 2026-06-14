package eris.compiler.symbol;

import eris.compiler.CompilerError;

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

    public void insert(String name, Symbol symbol) throws CompilerError {
        SymbolTable symbolTable = getSymbolTable();
        assert symbolTable != null;

        if (symbolTable.defines(name)) {
            throw new CompilerError("Already defined in this scope: " + name);
        }

        Symbol prevSymbol = symbolTable.lookup(name);
        if (prevSymbol instanceof TypeSymbol) {
            throw new CompilerError("Cannot shadow typename: " + name);
        }

        symbolTable.insert(name, symbol);
    }

    public SymbolTable getSymbolTable() {
        if (enteredScopes.isEmpty()) {
            return null;
        } else {
            return enteredScopes.peek();
        }
    }
}
