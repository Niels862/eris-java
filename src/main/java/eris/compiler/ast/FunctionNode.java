package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class FunctionNode extends Node {
    public final String name;
    public final List<StatementNode> statements;

    public SymbolTable scope;
    public FunctionSymbol symbol;

    public FunctionNode(Token name, List<StatementNode> statements) {
        super(name);
        this.name = name.text;
        this.statements = statements;
    }

    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
