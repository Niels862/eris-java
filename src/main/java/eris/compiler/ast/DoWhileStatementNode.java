package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class DoWhileStatementNode extends StatementNode {
    public final List<StatementNode> body;
    public final ExpressionNode condition;

    public SymbolTable scope;

    public DoWhileStatementNode(Token token, List<StatementNode> body, ExpressionNode condition) {
        super(token);
        this.body = body;
        this.condition = condition;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
