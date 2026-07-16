package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class FunctionNode extends Node implements DeclarationNode, ScopedNode {
    public final String name;
    public final List<StatementNode> statements;
    public final List<ParameterNode> parameters;
    public final TypeNode returnType;

    public final SymbolTable scope = new SymbolTable();
    public FunctionSymbol symbol;

    public FunctionNode(
            Token name,
            List<StatementNode> statements,
            List<ParameterNode> parameters,
            TypeNode returnType) {
        super(name);
        this.name = name.text;
        this.statements = statements;
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        NodeVisitor.accept(visitor, parameters);
        NodeVisitor.accept(visitor, statements);
        returnType.accept(visitor);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor, ScopeHandler scopeHandler) throws CompilerError {
        scopeHandler.enterScope(scope);
        NodeVisitor.accept(visitor, parameters);
        NodeVisitor.accept(visitor, statements);
        scopeHandler.leaveScope(scope);
        returnType.accept(visitor);
    }

    @Override
    public FunctionSymbol getSymbol() {
        return symbol;
    }
}
