package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

public class AssignmentStatementNode extends StatementNode {
    public final ExpressionNode target;
    public final ExpressionNode value;

    public AssignmentStatementNode(Token token, ExpressionNode target, ExpressionNode value) {
        super(token);
        this.target = target;
        this.value = value;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        target.accept(visitor);
        value.accept(visitor);
    }
}
