package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.ValueSymbol;

public class ParameterNode extends Node implements DeclarationNode {
    public final String name;
    public final TypeNode type;

    public ValueSymbol symbol;

    public ParameterNode(Token token, String name, TypeNode type) {
        super(token);
        this.name = name;
        this.type = type;
    }

    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        type.accept(visitor);
    }

    @Override
    public ValueSymbol getSymbol() {
        return symbol;
    }
}
