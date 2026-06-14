package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

public abstract class Node {
    public final int line;
    public final int column;

    Node(Token token) {
        this.line = token.line;
        this.column = token.column;
    }

    public abstract <T> T accept(NodeVisitor<T> visitor) throws CompilerError;

    public String toString() {
        return String.format("<%s>", getClass().getSimpleName());
    }
}
