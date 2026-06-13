package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.ast.*;
import eris.compiler.symbol.ScopeHandler;

public class SymbolBinder extends NodeVisitor<Void> {
    private final BuildModule buildModule;
    private final ModuleNode moduleNode;

    private final ScopeHandler scopeHandler = new ScopeHandler();

    public SymbolBinder(BuildModule buildModule, ModuleNode moduleNode) {
        this.buildModule = buildModule;
        this.moduleNode = moduleNode;
    }

    public void bindSymbols() {
        moduleNode.accept(this);
    }

    public Void defaultHandler(Node node) {
        throw new UnsupportedOperationException();
    }

    public Void visit(ModuleNode node) {
        node.globalScope = scopeHandler.enterNewScope();
        for (FunctionNode function : node.functions) {
            function.accept(this);
        }
        scopeHandler.leaveScope(node.globalScope);
        return null;
    }

    public Void visit(FunctionNode node) {
        node.scope = scopeHandler.enterNewScope();
        for (StatementNode statement : node.statements) {
            statement.accept(this);
        }
        scopeHandler.leaveScope(node.scope);
        return null;
    }

    public Void visit(ReturnStatementNode node) {
        return null;
    }

    public Void visit(IntegerNode node) {
        return null;
    }
}
