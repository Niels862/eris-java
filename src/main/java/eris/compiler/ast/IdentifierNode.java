package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.Symbol;

public class IdentifierNode extends ExpressionNode {
    public final String name;

    public Symbol symbol;

    public IdentifierNode(Token token, String name) {
        super(token);
        this.name = name;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
