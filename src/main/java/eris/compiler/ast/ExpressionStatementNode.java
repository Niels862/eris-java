package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

public class ExpressionStatementNode extends StatementNode {
    public final ExpressionNode expression;

    public ExpressionStatementNode(Token token, ExpressionNode expression) {
        super(token);
        this.expression = expression;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
