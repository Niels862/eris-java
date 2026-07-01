package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class LoopStatementNode extends StatementNode {
    public final List<StatementNode> body;

    public SymbolTable scope;

    public LoopStatementNode(Token token, List<StatementNode> body) {
        super(token);
        this.body = body;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
