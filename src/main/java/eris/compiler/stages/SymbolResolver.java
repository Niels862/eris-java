package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.symbol.*;
import eris.compiler.type.FunctionType;
import eris.compiler.type.Type;
import eris.compiler.type.TypeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolResolver {
    private final BuildModule module;

    private final ScopeHandler scopeHandler = new ScopeHandler();
    private final NodeHandler nodeHandler = new NodeHandler();
    private final TypeBuilder typeBuilder = new TypeBuilder();
    private final TypeInferrer typeInferrer = new TypeInferrer();

    public SymbolResolver(BuildModule module) {
        this.module = module;
    }

    public void resolveSymbols() throws CompilerError {
        module.moduleNode.accept(nodeHandler);
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
        public Void visit(ModuleNode node) throws CompilerError {
            super.visit(node);

            node.entrySymbol.setMeta(new FunctionType(Collections.emptyList(), TypeContext.instance.INT), Collections.emptyList());
            return null;
        }

        @Override
        public Void visit(ClassNode node) throws CompilerError {
            super.visit(node);

            List<ValueSymbol> attributes = new ArrayList<>();
            for (VariableNode attribute : node.attributes) {
                attributes.add(attribute.symbol);
            }

            node.symbol.setMeta(attributes, node.scope);
            return null;
        }

        @Override
        public Void visit(FunctionNode node) throws CompilerError {
            super.visit(node);

            List<Type> parameterTypes = new ArrayList<>();
            List<ValueSymbol> parameters = new ArrayList<>();
            for (ParameterNode parameter : node.parameters) {
                assert parameter.symbol.type != null;
                parameterTypes.add(parameter.symbol.type);
                parameters.add(parameter.symbol);
            }

            Type returnType = typeBuilder.build(node.returnType);
            FunctionType type = new FunctionType(parameterTypes, returnType);

            node.symbol.setMeta(type, parameters);
            return null;
        }

        @Override
        public Void visit(ParameterNode node) throws CompilerError {
            super.visit(node);
            node.symbol.setType(typeBuilder.build(node.type));
            node.symbol.setActive();
            return null;
        }

        @Override
        public Void visit(VariableNode node) throws CompilerError {
            super.visit(node);
            if (node.type != null) {
                node.symbol.setType(typeBuilder.build(node.type));
            } else {
                assert node.initialValue != null;
                node.symbol.setType(typeInferrer.infer(node.initialValue));
            }
            node.symbol.setActive();
            return null;
        }

        @Override
        public Void visit(IdentifierNode node) throws CompilerError {
            Symbol symbol = scopeHandler.getSymbolTable().lookup(node.name);
            assertValidSymbolReference(node, node.name, symbol);
            node.symbol = symbol;
            return null;
        }

        @Override
        public Void visit(NamedTypeNode node) throws CompilerError {
            Symbol symbol = scopeHandler.getSymbolTable().lookup(node.name);
            assertValidSymbolReference(node, node.name, symbol);
            if (symbol instanceof TypeSymbol typeSymbol) {
                node.symbol = typeSymbol;
            } else {
                throw new CompilerError(module, node.line, node.column, node.name + " is not a valid typename");
            }
            return null;
        }

        private void assertValidSymbolReference(Node node, String name, Symbol symbol) throws CompilerError {
            if (symbol == null) {
                throw new CompilerError(module, node.line, node.column, name + " is not defined in this scope");
            }

            if (!symbol.isActive()) {
                throw new CompilerError(module, node.line, node.column, name + " is accessed before declaration");
            }
        }
    }
}
