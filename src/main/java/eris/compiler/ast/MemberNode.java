package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.Symbol;

public class MemberNode extends ExpressionNode implements SymbolReferencingNode {
    public final ExpressionNode object;
    public final String member;

    public Symbol symbol;

    public MemberNode(Token token, ExpressionNode object, String member) {
        super(token);
        this.object = object;
        this.member = member;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        object.accept(visitor);
    }

    @Override
    public Symbol getReferencedSymbol() {
        return symbol;
    }
}
