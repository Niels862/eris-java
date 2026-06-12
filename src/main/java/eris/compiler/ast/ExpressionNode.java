package eris.compiler.ast;

import eris.compiler.Token;

public abstract class ExpressionNode extends Node {
    ExpressionNode(Token token) {
        super(token);
    }
}
