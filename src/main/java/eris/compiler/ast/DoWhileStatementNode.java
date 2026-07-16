package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class DoWhileStatementNode extends StatementNode implements ScopedNode {
    public final List<StatementNode> body;
    public final ExpressionNode condition;

    public final SymbolTable scope = new SymbolTable();

    public DoWhileStatementNode(Token token, List<StatementNode> body, ExpressionNode condition) {
        super(token);
        this.body = body;
        this.condition = condition;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        NodeVisitor.accept(visitor, body);
        condition.accept(visitor);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor, ScopeHandler scopeHandler) throws CompilerError {
        scopeHandler.enterScope(scope);
        acceptChildren(visitor);
        scopeHandler.leaveScope(scope);
    }
}
