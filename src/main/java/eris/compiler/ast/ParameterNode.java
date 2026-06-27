package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

public class ParameterNode extends Node {
    public final String name;

    public ParameterNode(Token token, String name) {
        super(token);
        this.name = name;
    }

    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
