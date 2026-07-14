package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.*;

public class SymbolBuilder extends NodeVisitor<Symbol> {
    private final BuildModule module;

    public SymbolBuilder(BuildModule module) {
        this.module = module;
    }

    public Symbol build(Node node) throws CompilerError {
        assert node instanceof DeclarationNode;
        return node.accept(this);
    }

    @Override
    public Symbol visit(ClassNode node) throws CompilerError {
        return build(node);
    }

    public ClassSymbol build(ClassNode node) throws CompilerError {
        return new ClassSymbol(node.name, module, node.line, node.column);
    }

    @Override
    public Symbol visit(FunctionNode node) throws CompilerError {
        return build(node);
    }

    public FunctionSymbol build(FunctionNode node) throws CompilerError {
        return new FunctionSymbol(node.name, module, node.line, node.column);
    }

    @Override
    public Symbol visit(ParameterNode node) throws CompilerError {
        return build(node);
    }

    public VariableSymbol build(ParameterNode node) throws CompilerError {
        return new VariableSymbol(node.name, module, node.line, node.column);
    }

    @Override
    public Symbol visit(VariableNode node) throws CompilerError {
        return build(node);
    }

    public VariableSymbol build(VariableNode node) throws CompilerError {
        return new VariableSymbol(node.name, module, node.line, node.column);
    }
}
