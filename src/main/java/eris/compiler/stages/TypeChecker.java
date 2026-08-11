package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.symbol.*;
import eris.compiler.type.ClassValueType;
import eris.compiler.type.FunctionType;
import eris.compiler.type.Type;

import java.util.List;

public class TypeChecker extends NodeVisitor<Void> {
    private final BuildModule module;

    private final TypeContext context = TypeContext.instance;

    public TypeChecker(BuildModule module) {
        this.module = module;
    }

    public void check() throws CompilerError {
        module.moduleNode.accept(this);
    }

    @Override
    public Void defaultHandler(Node node) throws CompilerError {
        if (node instanceof ExpressionNode) {
            super.defaultHandler(node);
        } else {
            node.acceptChildren(this);
        }
        return null;
    }

    @Override
    public Void visit(BinaryOperationNode node) throws CompilerError {
        node.acceptChildren(this);

        switch (node.operator) {
            case "===", "!==" -> {
                node.setInferredType(context.BOOL);
            }

            default -> {
                throw new UnsupportedOperationException("Unknown operator: " + node.operator);
            }
        }

        return null;
    }

    @Override
    public Void visit(CallNode node) throws CompilerError {
        node.acceptChildren(this);

        Symbol symbol = getCalledSymbol(node.function);

        FunctionType functionType;
        switch (symbol) {
            case FunctionSymbol functionSymbol -> {
                functionType = functionSymbol.type;
            }

            case ClassSymbol classSymbol -> {
                functionType = classSymbol.constructor.type;
            }

            default -> {
                throw node.function.error(module, "todo");
            }
        }

        checkArguments(node, functionType.parameterTypes, node.arguments);
        node.setInferredType(functionType.returnType);

        return null;
    }

    private Symbol getCalledSymbol(ExpressionNode node) throws CompilerError {
        switch (node) {
            case IdentifierNode identifierNode -> {
                return identifierNode.symbol;
            }

            default -> {
                String err = String.format("Cannot call value of type %s", node.getInferredType());
                throw node.error(module, err);
            }
        }
    }

    private void checkArguments(
            Node node,
            List<Type> parameterTypes,
            List<ExpressionNode> arguments) throws CompilerError {
        if (arguments.size() != parameterTypes.size()) {
            String err = String.format("Expected %d arguments, but got %d", parameterTypes.size(), arguments.size());
            throw node.error(module, err);
        }

        for (int i = 0; i < parameterTypes.size(); i++) {
            ExpressionNode argument = arguments.get(i);
            Type argumentType = argument.getInferredType();
            Type parameterType = parameterTypes.get(i);

            if (!isAssignableTo(parameterType, argumentType)) {
                String err = String.format("Expected `%s` value, but got `%s` value", parameterType, argumentType);
                throw argument.error(module, err);
            }
        }
    }

    @Override
    public Void visit(MemberNode node) throws CompilerError {
        node.acceptChildren(this);

        Type objectType = node.object.getInferredType();
        if (objectType instanceof ClassValueType classValueType) {
            Symbol memberSymbol = classValueType.symbol.symbolTable.lookup(node.member);

            if (memberSymbol != null) {
                Type type;
                switch (memberSymbol) {
                    case ValueSymbol valueSymbol -> {
                        type = valueSymbol.type;
                    }

                    case FunctionSymbol functionSymbol -> {
                        type = functionSymbol.type;
                    }

                    default -> {
                        throw new UnsupportedOperationException("Unknown member type: " + memberSymbol);
                    }
                }

                node.setInferredType(type);
                node.symbol = memberSymbol;
            } else {
                throw node.error(module, String.format("`%s` value has no attribute '%s'", objectType, node.member));
            }
        } else {
            throw node.error(module, String.format("Cannot access member of `%s` value", objectType));
        }

        return null;
    }

    @Override
    public Void visit(IdentifierNode node) throws CompilerError {
        switch (node.symbol) {
            case ValueSymbol valueSymbol -> {
                node.setInferredType(valueSymbol.type);
            }

            case FunctionSymbol functionSymbol -> {
                node.setInferredType(functionSymbol.type);
            }

            case ClassSymbol classSymbol -> {
                node.setInferredType(classSymbol.constructor.type);
            }

            default -> throw new IllegalStateException("Unexpected value: " + node.symbol);
        }
        return null;
    }

    @Override
    public Void visit(IntegerLiteralNode node) throws CompilerError {
        node.setInferredType(context.INT);
        return null;
    }

    @Override
    public Void visit(BooleanLiteralNode node) throws CompilerError {
        node.setInferredType(context.BOOL);
        return null;
    }

    @Override
    public Void visit(StringLiteralNode node) throws CompilerError {
        node.setInferredType(context.STRING);
        return null;
    }

    @Override
    public Void visit(NullLiteralNode node) throws CompilerError {
        node.setInferredType(context.NULL);
        return null;
    }

    private boolean isAssignableTo(Type target, Type value) {
        if (target == value) {
            return true;
        }

        return false;
    }
}
