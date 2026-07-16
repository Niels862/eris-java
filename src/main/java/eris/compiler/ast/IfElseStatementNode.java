package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class IfElseStatementNode extends StatementNode implements ScopedNode {
    public final ExpressionNode condition;
    public final List<StatementNode> thenBody;
    public final List<StatementNode> elseBody;

    public final SymbolTable thenScope = new SymbolTable();
    public final SymbolTable elseScope = new SymbolTable();

    public IfElseStatementNode(
            Token token, ExpressionNode condition,
            List<StatementNode> trueBranch,
            List<StatementNode> falseBranch) {
        super(token);
        this.condition = condition;
        this.thenBody = trueBranch;
        this.elseBody = falseBranch;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        condition.accept(visitor);
        NodeVisitor.accept(visitor, thenBody);
        NodeVisitor.accept(visitor, elseBody);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor, ScopeHandler scopeHandler) throws CompilerError {
        scopeHandler.enterScope(thenScope);
        condition.accept(visitor);
        NodeVisitor.accept(visitor, thenBody);
        scopeHandler.leaveScope(thenScope);

        scopeHandler.enterScope(elseScope);
        NodeVisitor.accept(visitor, elseBody);
        scopeHandler.leaveScope(elseScope);
    }
}
