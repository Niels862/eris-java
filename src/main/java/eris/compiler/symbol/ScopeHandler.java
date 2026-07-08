package eris.compiler.symbol;

import eris.compiler.CompilerError;

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

    public void enterScope(SymbolTable symbolTable) {
        assert symbolTable != null;
        enteredScopes.push(symbolTable);
    }

    public void leaveScope(SymbolTable symbolTable) {
        SymbolTable top = enteredScopes.pop();
        assert top == symbolTable;
    }

    public void insert(String name, Symbol symbol) throws CompilerError {
        assert symbol != null;

        SymbolTable symbolTable = getSymbolTable();
        assert symbolTable != null;

        if (symbolTable.defines(name)) {
            throw symbol.error("Already defined in this scope: " + name);
        }

        Symbol prevSymbol = symbolTable.lookup(name);
        if (prevSymbol instanceof TypeSymbol) {
            throw symbol.error("Cannot shadow typename: " + name);
        }

        symbolTable.insert(name, symbol);
    }

    public void insertAll(SymbolTable symbolTable) throws CompilerError {
        for (Map.Entry<String, Symbol> entry : symbolTable.getSymbols().entrySet()) {
            insert(entry.getKey(), entry.getValue());
        }
    }

    public SymbolTable getSymbolTable() {
        if (enteredScopes.isEmpty()) {
            return null;
        } else {
            return enteredScopes.peek();
        }
    }
}
