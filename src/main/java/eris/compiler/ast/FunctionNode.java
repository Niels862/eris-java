package eris.compiler.ast;

import eris.compiler.Token;

import java.util.List;

public class FunctionNode extends Node {
    public final String name;
    public final List<StatementNode> statements;

    public FunctionNode(Token name, List<StatementNode> statements) {
        super(name);
        this.name = name.text;
        this.statements = statements;
    }

    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
