package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.refinement.Refinement;
import eris.compiler.symbol.Symbol;

public class IdentifierNode extends ExpressionNode implements SymbolReferencingNode {
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

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {}

    @Override
    public Symbol getReferencedSymbol() {
        return symbol;
    }
}
