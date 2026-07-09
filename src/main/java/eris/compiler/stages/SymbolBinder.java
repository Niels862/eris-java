package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.symbol.*;
import eris.compiler.type.FunctionType;
import eris.compiler.type.NullableType;
import eris.compiler.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolBinder extends NodeVisitor<Symbol> {
    private final BuildModule module;
    private final ModuleNode moduleNode;
    private final SymbolTable definitions;

    private final ScopeHandler scopeHandler = new ScopeHandler();
    private final TypeContext context = TypeContext.instance;
    private final TypeBuilder typeBuilder = new TypeBuilder();

    public SymbolBinder(BuildModule module, ModuleNode moduleNode, SymbolTable definitions) {
        this.module = module;
        this.moduleNode = moduleNode;
        this.definitions = definitions;
    }

    public void bindSymbols() throws CompilerError {
        scopeHandler.enterScope(context.symbolTable);
        moduleNode.accept(this);
        scopeHandler.leaveScope(context.symbolTable);
    }

    @Override
    public Symbol visit(ModuleNode node) throws CompilerError {
        node.globalScope = scopeHandler.enterNewScope();

        scopeHandler.insertAll(definitions);

        for (ClassNode classNode : node.classes) {
            classNode.accept(this);
        }

        for (FunctionNode functionNode : node.functions) {
            functionNode.accept(this);
        }

        scopeHandler.leaveScope(node.globalScope);

        FunctionType entryFunctionType = new FunctionType(Collections.emptyList(), context.INT);
        node.entrySymbol = new FunctionSymbol("$entry", module, node.line, node.column);
        node.entrySymbol.finalize(entryFunctionType, Collections.emptyList());
        return null;
    }

    @Override
    public Symbol visit(ClassNode node) throws CompilerError {
        SymbolTable members = scopeHandler.enterNewScope();
        List<VariableSymbol> attributes = new ArrayList<>();

        for (VariableNode variable : node.attributes) {
            declare(variable);
            attributes.add(variable.symbol);
        }

        node.symbol.finalize(attributes);
        scopeHandler.leaveScope(members);
        return node.symbol;
    }

    @Override
    public Symbol visit(FunctionNode node) throws CompilerError {
        node.scope = scopeHandler.enterNewScope();

        List<Type> parameterTypes = new ArrayList<>();
        List<VariableSymbol> parameters = new ArrayList<>();
        for (ParameterNode parameter : node.parameters) {
            declare(parameter);
            parameterTypes.add(parameter.symbol.staticType);
            parameters.add(parameter.symbol);
        }

        for (StatementNode statement : node.statements) {
            declareAndPropagate(statement);
        }
        scopeHandler.leaveScope(node.scope);

        Type returnType = buildType(node.returnType);
        FunctionType type = new FunctionType(parameterTypes, returnType);
        node.symbol.finalize(type, parameters);
        return node.symbol;
    }

    @Override
    public Symbol visit(ParameterNode node) throws CompilerError {
        Type type = buildType(node.type);
        node.symbol = new VariableSymbol(node.name, module, node.line, node.column, type);
        return node.symbol;
    }

    @Override
    public Symbol visit(VariableNode node) throws CompilerError {
        Type type = null;
        if (node.type != null) {
            type = buildType(node.type);
        } else if (node.initialValue == null) {
            throw new CompilerError(
                    module, node.line, node.column,
                    String.format("Cannot infer type of %s: missing initial value", node.name));
        }
        node.symbol = new VariableSymbol(node.name, module, node.line, node.column, type);
        return node.symbol;
    }

    @Override
    public Symbol visit(AssignmentStatementNode node) throws CompilerError {
        propagate(node.target);
        propagate(node.value);
        return null;
    }

    @Override
    public Symbol visit(IfElseStatementNode node) throws CompilerError {
        node.thenScope = scopeHandler.enterNewScope();
        propagate(node.condition);
        for (StatementNode statement : node.thenBody) {
            declareAndPropagate(statement);
        }
        scopeHandler.leaveScope(node.thenScope);

        node.elseScope = scopeHandler.enterNewScope();
        for (StatementNode statement : node.elseBody) {
            declareAndPropagate(statement);
        }
        scopeHandler.leaveScope(node.elseScope);
        return null;
    }

    @Override
    public Symbol visit(WhileStatementNode node) throws CompilerError {
        node.scope = scopeHandler.enterNewScope();
        propagate(node.condition);
        for (StatementNode statement : node.body) {
            declareAndPropagate(statement);
        }
        scopeHandler.leaveScope(node.scope);
        return null;
    }

    @Override
    public Symbol visit(DoWhileStatementNode node) throws CompilerError {
        node.scope = scopeHandler.enterNewScope();
        for (StatementNode statement : node.body) {
            declareAndPropagate(statement);
        }
        propagate(node.condition);
        scopeHandler.leaveScope(node.scope);
        return null;
    }

    @Override
    public Symbol visit(LoopStatementNode node) throws CompilerError {
        node.scope = scopeHandler.enterNewScope();
        for (StatementNode statement : node.body) {
            declareAndPropagate(statement);
        }
        scopeHandler.leaveScope(node.scope);
        return null;
    }

    @Override
    public Symbol visit(ExpressionStatementNode node) throws CompilerError {
        propagate(node.expression);
        return null;
    }

    @Override
    public Symbol visit(ReturnStatementNode node) throws CompilerError {
        if (node.value != null) {
            propagate(node.value);
        }
        return null;
    }

    @Override
    public Symbol visit(BinaryOperationNode node) throws CompilerError {
        propagate(node.left);
        propagate(node.right);
        return null;
    }

    @Override
    public Symbol visit(CallNode node) throws CompilerError {
        for (ExpressionNode expression : node.arguments) {
            propagate(expression);
        }
        return null;
    }

    @Override
    public Symbol visit(IdentifierNode node) throws CompilerError {
        return null;
    }

    @Override
    public Symbol visit(IntegerLiteralNode node) throws CompilerError {
        return null;
    }

    @Override
    public Symbol visit(BooleanLiteralNode node) throws CompilerError {
        return null;
    }

    @Override
    public Symbol visit(StringLiteralNode node) throws CompilerError {
        return null;
    }

    @Override
    public Symbol visit(NullLiteralNode node) throws CompilerError {
        return null;
    }

    private Symbol declare(Node node) throws CompilerError {
        Symbol symbol = node.accept(this);
        assert symbol != null;
        scopeHandler.insert(symbol.name, symbol);
        return symbol;
    }

    private void propagate(Node node) throws CompilerError {
        Symbol symbol = node.accept(this);
        assert symbol == null;
    }

    private Symbol declareAndPropagate(Node node) throws CompilerError {
        Symbol symbol = node.accept(this);
        if (symbol != null) {
            scopeHandler.insert(symbol.name, symbol);
        }
        return symbol;
    }

    private Type buildType(TypeNode node) throws CompilerError {
        Type type = node.accept(typeBuilder);
        assert type != null;
        return type;
    }

    private class TypeBuilder extends NodeVisitor<Type> {
        @Override
        public Type visit(NamedTypeNode node) throws CompilerError {
            Symbol symbol = scopeHandler.getSymbolTable().lookup(node.name);
            if (symbol instanceof ClassSymbol classSymbol) {
                return classSymbol.valueType;
            } else {
                throw new CompilerError(module, node.line, node.column, String.format("%s is not a typename", node.name));
            }
        }

        @Override
        public Type visit(NullableTypeNode node) throws CompilerError {
            return new NullableType(node.type.accept(this));
        }
    }
}
