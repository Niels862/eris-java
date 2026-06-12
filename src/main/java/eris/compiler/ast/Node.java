package eris.compiler.ast;

import eris.compiler.Token;

public abstract class Node {
    final int line;
    final int column;

    Node() {
        this.line = 0;
        this.column = 0;
    }

    Node(Token token) {
        this.line = token.line;
        this.column = token.column;
    }

    public abstract <T> T accept(NodeVisitor<T> visitor);
}
