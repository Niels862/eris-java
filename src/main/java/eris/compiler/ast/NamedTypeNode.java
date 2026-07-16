package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.TypeSymbol;

public class NamedTypeNode extends TypeNode {
    public final String name;

    public TypeSymbol symbol;

    public NamedTypeNode(Token token, String name) {
        super(token);
        this.name = name;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) {}
}
