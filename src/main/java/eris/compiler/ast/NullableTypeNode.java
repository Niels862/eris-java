package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;

public class NullableTypeNode extends TypeNode {
    public final TypeNode type;

    public NullableTypeNode(Token token, TypeNode type) {
        super(token);
        this.type = type;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
