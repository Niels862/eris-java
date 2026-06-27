package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.VariableSymbol;

public class VariableNode extends StatementNode {
    public final String name;
    public final ExpressionNode initialValue;
    public final TypeNode type;

    public VariableSymbol symbol;

    public VariableNode(Token token, String name, ExpressionNode initialValue, TypeNode type) {
        super(token);
        this.name = name;
        this.initialValue = initialValue;
        this.type = type;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
