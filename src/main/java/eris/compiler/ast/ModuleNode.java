package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class ModuleNode extends Node implements ScopedNode {
    public final List<ClassNode> classes;
    public final List<FunctionNode> functions;

    public final SymbolTable globalScope = new SymbolTable();
    public FunctionSymbol entrySymbol;

    public ModuleNode(Token token, List<ClassNode> classes, List<FunctionNode> functions) {
        super(token);
        this.classes = classes;
        this.functions = functions;
    }

    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        NodeVisitor.accept(visitor, classes);
        NodeVisitor.accept(visitor, functions);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor, ScopeHandler scopeHandler) throws CompilerError {
        scopeHandler.enterScope(globalScope);
        NodeVisitor.accept(visitor, classes);
        NodeVisitor.accept(visitor, functions);
        scopeHandler.leaveScope(globalScope);
    }
}
