package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.symbol.*;
import eris.compiler.type.FunctionType;
import eris.compiler.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolBinder extends NodeVisitor<Void> {
    private final BuildModule module;
    private final ModuleNode moduleNode;

    private final ScopeHandler scopeHandler = new ScopeHandler();
    private final TypeContext context = TypeContext.instance;
    private final TypeBuilder typeBuilder = new TypeBuilder();

    public SymbolBinder(BuildModule module, ModuleNode moduleNode) {
        this.module = module;
        this.moduleNode = moduleNode;
    }

    public void bindSymbols() throws CompilerError {
        scopeHandler.enterScope(context.symbolTable);
        moduleNode.accept(this);
        scopeHandler.leaveScope(context.symbolTable);
    }

    @Override
    public Void visit(ModuleNode node) throws CompilerError {
        node.globalScope = scopeHandler.enterNewScope();
        for (FunctionNode function : node.functions) {
            function.accept(this);
        }
        scopeHandler.leaveScope(node.globalScope);

        FunctionType entryFunctionType = new FunctionType(Collections.emptyList(), context.INT);
        node.entrySymbol = new FunctionSymbol("$entry", module, node.line, node.column, entryFunctionType);
        return null;
    }

    @Override
    public Void visit(FunctionNode node) throws CompilerError {
        node.scope = scopeHandler.enterNewScope();

        List<Type> parameterTypes = new ArrayList<Type>();
        for (ParameterNode parameter : node.parameters) {
            parameter.accept(this);
            parameterTypes.add(parameter.symbol.staticType);
        }

        for (StatementNode statement : node.statements) {
            statement.accept(this);
        }
        scopeHandler.leaveScope(node.scope);

        Type returnType = buildType(node.returnType);
        FunctionType type = new FunctionType(parameterTypes, returnType);
        node.symbol = new FunctionSymbol(node.name, module, node.line, node.column, type);
        scopeHandler.insert(node.name, node.symbol);
        return null;
    }

    @Override
    public Void visit(ParameterNode node) throws CompilerError {
        Type type = buildType(node.type);
        node.symbol = new VariableSymbol(node.name, module, node.line, node.column, type);
        scopeHandler.insert(node.name, node.symbol);
        return null;
    }

    @Override
    public Void visit(VariableNode node) throws CompilerError {
        Type type = null;
        if (node.type != null) {
            type = buildType(node.type);
        } else if (node.initialValue == null) {
            throw new CompilerError(
                    module, node.line, node.column,
                    String.format("Cannot infer type of %s: missing initial value", node.name));
        }
        node.symbol = new VariableSymbol(node.name, module, node.line, node.column, type);
        scopeHandler.insert(node.name, node.symbol);
        return null;
    }

    @Override
    public Void visit(AssignmentStatementNode node) throws CompilerError {
        node.value.accept(this);
        return null;
    }

    @Override
    public Void visit(IfElseStatementNode node) throws CompilerError {
        node.thenScope = scopeHandler.enterNewScope();
        node.condition.accept(this);
        for (StatementNode statement : node.thenBody) {
            statement.accept(this);
        }
        scopeHandler.leaveScope(node.thenScope);

        node.elseScope = scopeHandler.enterNewScope();
        for (StatementNode statement : node.elseBody) {
            statement.accept(this);
        }
        scopeHandler.leaveScope(node.elseScope);
        return null;
    }

    @Override
    public Void visit(WhileStatementNode node) throws CompilerError {
        node.scope = scopeHandler.enterNewScope();
        node.condition.accept(this);
        for (StatementNode statement : node.body) {
            statement.accept(this);
        }
        scopeHandler.leaveScope(node.scope);
        return null;
    }

    @Override
    public Void visit(DoWhileStatementNode node) throws CompilerError {
        node.scope = scopeHandler.enterNewScope();
        for (StatementNode statement : node.body) {
            statement.accept(this);
        }
        node.condition.accept(this);
        scopeHandler.leaveScope(node.scope);
        return null;
    }

    @Override
    public Void visit(LoopStatementNode node) throws CompilerError {
        node.scope = scopeHandler.enterNewScope();
        for (StatementNode statement : node.body) {
            statement.accept(this);
        }
        scopeHandler.leaveScope(node.scope);
        return null;
    }

    @Override
    public Void visit(ExpressionStatementNode node) throws CompilerError {
        node.expression.accept(this);
        return null;
    }

    @Override
    public Void visit(ReturnStatementNode node) throws CompilerError {
        if (node.value != null) {
            node.value.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(BinaryOperationNode node) throws CompilerError {
        node.left.accept(this);
        node.right.accept(this);
        return null;
    }

    @Override
    public Void visit(CallNode node) throws CompilerError {
        for (ExpressionNode expression : node.arguments) {
            expression.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(IdentifierNode node) throws CompilerError {
        return null;
    }

    @Override
    public Void visit(IntegerLiteralNode node) throws CompilerError {
        return null;
    }

    @Override
    public Void visit(BooleanLiteralNode node) throws CompilerError {
        return null;
    }

    @Override
    public Void visit(StringLiteralNode node) throws CompilerError {
        return null;
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
    }
}
