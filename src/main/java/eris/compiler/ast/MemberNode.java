package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.Symbol;
import eris.compiler.symbol.ValueSymbol;

public class MemberNode extends ExpressionNode {
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
}
