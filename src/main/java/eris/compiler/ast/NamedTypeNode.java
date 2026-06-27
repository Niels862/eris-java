package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

public class NamedTypeNode extends TypeNode {
    public final String name;

    public NamedTypeNode(Token token, String name) {
        super(token);
        this.name = name;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
