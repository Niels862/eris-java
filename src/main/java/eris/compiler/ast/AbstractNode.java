package eris.compiler.ast;

import eris.compiler.NodeVisitor;
import eris.compiler.Token;

public abstract class AbstractNode {
    final int line;
    final int column;

    AbstractNode() {
        this.line = 0;
        this.column = 0;
    }

    AbstractNode(Token token) {
        this.line = token.line;
        this.column = token.column;
    }

    public abstract <T> T accept(NodeVisitor<T> visitor);
}
