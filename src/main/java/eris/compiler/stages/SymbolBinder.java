package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.*;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.VariableSymbol;

public class SymbolBinder extends NodeVisitor<Void> {
    private final BuildModule module;
    private final ModuleNode moduleNode;

    private final ScopeHandler scopeHandler = new ScopeHandler();

    public SymbolBinder(BuildModule module, ModuleNode moduleNode) {
        this.module = module;
        this.moduleNode = moduleNode;
    }

    public void bindSymbols() throws CompilerError {
        moduleNode.accept(this);
        System.out.println(moduleNode.globalScope);
    }

    @Override
    public Void visit(ModuleNode node) throws CompilerError {
        node.globalScope = scopeHandler.enterNewScope();
        for (FunctionNode function : node.functions) {
            function.accept(this);
        }
        scopeHandler.leaveScope(node.globalScope);

        node.entrySymbol = new FunctionSymbol("$entry", module, node.line, node.column);
        return null;
    }

    @Override
    public Void visit(FunctionNode node) throws CompilerError {
        node.scope = scopeHandler.enterNewScope();
        for (ParameterNode parameter : node.parameters) {
            parameter.accept(this);
        }
        for (StatementNode statement : node.statements) {
            statement.accept(this);
        }
        scopeHandler.leaveScope(node.scope);

        node.symbol = new FunctionSymbol(node.name, module, node.line, node.column);
        scopeHandler.insert(node.name, node.symbol);
        return null;
    }

    @Override
    public Void visit(ParameterNode node) throws CompilerError {
        node.symbol = new VariableSymbol(node.name, module, node.line, node.column);
        scopeHandler.insert(node.name, node.symbol);
        return null;
    }

    @Override
    public Void visit(DeclarationNode node) throws CompilerError {
        node.symbol = new VariableSymbol(node.name, module, node.line, node.column);
        scopeHandler.insert(node.name, node.symbol);
        return null;
    }

    @Override
    public Void visit(ReturnStatementNode node) {
        return null;
    }

    @Override
    public Void visit(IntegerNode node) {
        return null;
    }
}
