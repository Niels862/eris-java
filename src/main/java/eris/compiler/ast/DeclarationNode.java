package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.VariableSymbol;

public class DeclarationNode extends StatementNode {
    public final String name;
    public final ExpressionNode initialValue;

    public VariableSymbol symbol;

    public DeclarationNode(Token token, String name, ExpressionNode initialValue) {
        super(token);
        this.name = name;
        this.initialValue = initialValue;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
