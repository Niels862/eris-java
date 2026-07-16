package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

import java.util.List;

public class CallNode extends ExpressionNode {
    public final ExpressionNode function;
    public final List<ExpressionNode> arguments;

    public CallNode(Token token, ExpressionNode function, List<ExpressionNode> arguments) {
        super(token);
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        function.accept(visitor);
        NodeVisitor.accept(visitor, arguments);
    }
}
