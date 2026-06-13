package eris.compiler.ast;

import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class ModuleNode extends Node {
    public final List<FunctionNode> functions;

    public SymbolTable globalScope;

    public ModuleNode(List<FunctionNode> functions) {
        this.functions = functions;
    }

    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
