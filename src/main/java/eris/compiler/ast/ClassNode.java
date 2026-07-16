package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.ClassSymbol;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class ClassNode extends Node implements DeclarationNode, ScopedNode {
    public final String name;
    public final List<VariableNode> attributes;

    public final SymbolTable scope = new SymbolTable();
    public ClassSymbol symbol;

    public ClassNode(Token token, String name, List<VariableNode> attributes) {
        super(token);
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        NodeVisitor.accept(visitor, attributes);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor, ScopeHandler scopeHandler) throws CompilerError {
        scopeHandler.enterScope(scope);
        acceptChildren(visitor);
        scopeHandler.leaveScope(scope);
    }

    @Override
    public ClassSymbol getSymbol() {
        return symbol;
    }
}
