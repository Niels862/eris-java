package eris.compiler.symbol;

import eris.compiler.CompilerError;

import java.util.Map;
import java.util.Stack;

public class ScopeHandler {
    private final Stack<SymbolTable> enteredScopes = new Stack<>();

    private static final SymbolTable nullTable = new SymbolTable();

    public void enterScope(SymbolTable symbolTable) {
        assert symbolTable != null;
        if (!symbolTable.active()) {
            symbolTable.setParent(getSymbolTable());
        }
        enteredScopes.push(symbolTable);
    }

    public void leaveScope(SymbolTable symbolTable) {
        SymbolTable top = enteredScopes.pop();
        assert top == symbolTable;
    }

    public void declare(String name, Symbol symbol) throws CompilerError {
        assert symbol != null;

        SymbolTable symbolTable = getSymbolTable();
        assert symbolTable != nullTable;

        if (symbolTable.defines(name)) {
            throw symbol.error("Already defined in this scope: " + name);
        }

        Symbol prevSymbol = symbolTable.lookup(name);
        if (prevSymbol instanceof TypeSymbol) {
            throw symbol.error("Cannot shadow typename: " + name);
        }

        symbolTable.insert(name, symbol);
    }

    public SymbolTable getSymbolTable() {
        if (enteredScopes.isEmpty()) {
            return nullTable;
        } else {
            return enteredScopes.peek();
        }
    }
}
