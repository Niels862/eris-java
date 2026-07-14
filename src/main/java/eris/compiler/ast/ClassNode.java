package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.ClassSymbol;

import java.util.List;

public class ClassNode extends Node implements DeclarationNode {
    public final String name;
    public final List<VariableNode> attributes;

    public ClassSymbol symbol;

    public ClassNode(Token token, String name, List<VariableNode> attributes) {
        super(token);
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public ClassSymbol getSymbol() {
        return symbol;
    }
}
