package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.ValueSymbol;

public class VariableNode extends StatementNode implements DeclarationNode {
    public final String name;
    public final ExpressionNode initialValue;
    public final TypeNode type;

    public ValueSymbol symbol;

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

    @Override
    public <T> void acceptChildren(NodeVisitor<T> visitor) throws CompilerError {
        if (initialValue != null) {
            initialValue.accept(visitor);
        }
        if (type != null) {
            type.accept(visitor);
        }
    }

    @Override
    public ValueSymbol getSymbol() {
        return symbol;
    }
}
