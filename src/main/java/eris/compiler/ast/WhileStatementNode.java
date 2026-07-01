package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class WhileStatementNode extends StatementNode {
    public final ExpressionNode condition;
    public final List<StatementNode> body;

    public SymbolTable scope;

    public WhileStatementNode(Token token, ExpressionNode condition, List<StatementNode> body) {
        super(token);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
