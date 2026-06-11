package eris.compiler.ast;

import eris.compiler.NodeVisitor;

import javax.annotation.Nullable;

public class ReturnStatementNode extends StatementNode {
    @Nullable
    public final ExpressionNode value;

    public ReturnStatementNode(@Nullable ExpressionNode value) {
        this.value = value;
    }

    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
