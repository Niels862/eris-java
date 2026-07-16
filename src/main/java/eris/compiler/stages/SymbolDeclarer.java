package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.SymbolBuilder;
import eris.compiler.symbol.SymbolTable;

public class SymbolDeclarer {
    private final BuildModule module;

    private final SymbolBuilder builder;
    private final ScopeHandler scopeHandler = new ScopeHandler();
    private final NodeHandler nodeHandler = new NodeHandler();

    public SymbolDeclarer(BuildModule module) {
        this.module = module;
        this.builder = new SymbolBuilder(module);
    }

    public void declareSymbols() throws CompilerError {
        SymbolTable builtins = TypeContext.instance.symbolTable;
        scopeHandler.enterScope(builtins);
        module.moduleNode.accept(nodeHandler);
        scopeHandler.leaveScope(builtins);
    }

    private class NodeHandler extends NodeVisitor<Void> {
        @Override
        public Void defaultHandler(Node node) throws CompilerError {
            if (node instanceof ScopedNode scopedNode) {
                scopedNode.acceptChildren(this, scopeHandler);
            } else {
                node.acceptChildren(this);
            }
            return null;
        }

        @Override
        public Void visit(ClassNode node) throws CompilerError {
            node.symbol = builder.build(node);
            scopeHandler.declare(node.name, node.symbol);
            super.visit(node);
            return null;
        }

        @Override
        public Void visit(FunctionNode node) throws CompilerError {
            node.symbol = builder.build(node);
            scopeHandler.declare(node.name, node.symbol);
            super.visit(node);
            return null;
        }

        public Void visit(ParameterNode node) throws CompilerError {
            node.symbol = builder.build(node);
            scopeHandler.declare(node.name, node.symbol);
            super.visit(node);
            return null;
        }

        public Void visit(VariableNode node) throws CompilerError {
            node.symbol = builder.build(node);
            scopeHandler.declare(node.name, node.symbol);
            super.visit(node);
            return null;
        }
    }
}
