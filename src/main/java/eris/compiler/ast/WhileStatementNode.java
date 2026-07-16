package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class WhileStatementNode extends StatementNode implements ScopedNode {
    public final ExpressionNode condition;
    public final List<StatementNode> body;

    public final SymbolTable scope = new SymbolTable();

    public WhileStatementNode(Token token, ExpressionNode condition, List<StatementNode> body) {
        super(token);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        condition.accept(visitor);
        NodeVisitor.accept(visitor, body);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor, ScopeHandler scopeHandler) throws CompilerError {
        scopeHandler.enterScope(scope);
        condition.accept(visitor);
        NodeVisitor.accept(visitor, body);
        scopeHandler.leaveScope(scope);
    }
}
