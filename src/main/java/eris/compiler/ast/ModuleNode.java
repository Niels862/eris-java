package eris.compiler.ast;

import java.util.List;

public class ModuleNode extends Node {
    public final List<FunctionNode> functions;

    public ModuleNode(List<FunctionNode> functions) {
        this.functions = functions;
    }

    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
