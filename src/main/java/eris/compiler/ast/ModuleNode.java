package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class ModuleNode extends Node {
    public final List<ClassNode> classes;
    public final List<FunctionNode> functions;

    public SymbolTable globalScope;
    public FunctionSymbol entrySymbol;

    public ModuleNode(Token token, List<ClassNode> classes, List<FunctionNode> functions) {
        super(token);
        this.classes = classes;
        this.functions = functions;
    }

    public <T> T accept(NodeVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }
}
