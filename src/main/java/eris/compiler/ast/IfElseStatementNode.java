package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class IfElseStatementNode extends StatementNode {
    public final ExpressionNode condition;
    public final List<StatementNode> thenBody;
    public final List<StatementNode> elseBody;

    public SymbolTable thenScope;
    public SymbolTable elseScope;

    public IfElseStatementNode(
            Token token, ExpressionNode condition,
            List<StatementNode> trueBranch,
            List<StatementNode> falseBranch) {
        super(token);
        this.condition = condition;
        this.thenBody = trueBranch;
        this.elseBody = falseBranch;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
