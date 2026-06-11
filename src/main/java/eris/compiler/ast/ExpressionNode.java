package eris.compiler.ast;

import eris.compiler.Token;

public abstract class ExpressionNode extends AbstractNode {
    ExpressionNode(Token token) {
        super(token);
    }
}
