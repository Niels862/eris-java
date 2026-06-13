package eris.compiler.ast;

import eris.compiler.Token;

public abstract class StatementNode extends Node {
    public StatementNode(Token token) {
        super(token);
    }
}
