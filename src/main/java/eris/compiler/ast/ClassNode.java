package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.ClassSymbol;

public class ClassNode extends Node {
    public final String name;

    public ClassSymbol symbol;

    public ClassNode(Token token, String name) {
        super(token);
        this.name = name;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
