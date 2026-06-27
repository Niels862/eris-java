package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.VariableSymbol;

public class ParameterNode extends Node {
    public final String name;

    public VariableSymbol symbol;

    public ParameterNode(Token token, String name) {
        super(token);
        this.name = name;
    }

    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
