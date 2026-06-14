package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

import javax.annotation.Nullable;

public class ReturnStatementNode extends StatementNode {
    @Nullable
    public final ExpressionNode value;

    public ReturnStatementNode(Token token, @Nullable ExpressionNode value) {
        super(token);
        this.value = value;
    }

    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
