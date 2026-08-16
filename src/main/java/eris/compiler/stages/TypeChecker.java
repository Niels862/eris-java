package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.symbol.*;
import eris.compiler.type.*;

public class TypeChecker {
    private final BuildModule module;
    private final NodeHandler handler = new NodeHandler();

    public TypeChecker(BuildModule module) {
        this.module = module;
    }

    public void check() throws CompilerError {
        module.moduleNode.accept(handler);
    }

    private class NodeHandler extends NodeVisitor<Void> {
        private final TypeInferrer inferrer = new TypeInferrer(true);
        private final TypeContext context = TypeContext.instance;

        private FunctionNode function;

        private Type infer(ExpressionNode node) throws CompilerError {
            if (!node.hasInferredType()) {
                Type type = inferrer.infer(node);
                if (type instanceof InferenceType) {
                    throw node.error(module, "Could not infer type: " + type);
                }
                node.setInferredType(type);
                return type;
            } else {
                return node.getInferredType();
            }
        }

        @Override
        public Void defaultHandler(Node node) throws CompilerError {
            if (node instanceof ExpressionNode expressionNode) {
                infer(expressionNode);
            } else {
                node.acceptChildren(this);
            }
            return null;
        }

        public Void visit(ClassNode node) throws CompilerError {
            return defaultHandler(node);
        }

        public Void visit(FunctionNode node) throws CompilerError {
            FunctionNode previous = function;
            function = node;

            NodeHandler.accept(this, node.statements);

            function = previous;
            return null;
        }

        public Void visit(VariableNode node) throws CompilerError {
            if (node.initialValue != null) {
                Type initialValue = infer(node.initialValue);
                if (node.symbol.type instanceof InferenceType) {
                    node.symbol.setType(initialValue);
                }

                if (!isAssignableTo(node.symbol.type, initialValue)) {
                    String err = String.format("Cannot use `%s` value as initial value of '%s' of type `%s`",
                            initialValue, node.name, node.symbol.type);
                    throw node.error(module, err);
                }
            }
            return null;
        }

        public Void visit(AssignmentStatementNode node) throws CompilerError {
            Type value = infer(node.value);
            Type target = infer(node.target);
            if (!isAssignableTo(target, value)) {
                throw node.error(module, String.format("Cannot assign `%s` value to `%s` target", value, target));
            }
            return null;
        }

        public Void visit(IfElseStatementNode node) throws CompilerError {
            visitCondition(node.condition);
            NodeHandler.accept(this, node.thenBody);
            NodeHandler.accept(this, node.elseBody);
            return null;
        }

        public Void visit(WhileStatementNode node) throws CompilerError {
            visitCondition(node.condition);
            NodeHandler.accept(this, node.body);
            return null;
        }

        public Void visit(DoWhileStatementNode node) throws CompilerError {
            visitCondition(node.condition);
            NodeHandler.accept(this, node.body);
            return null;
        }

        private void visitCondition(ExpressionNode node) throws CompilerError {
            Type condition = infer(node);
            if (!isAssignableTo(context.BOOL, condition)) {
                String err = String.format("Cannot use `%s` value as condition of type `%s`",
                        condition, context.BOOL);
                throw node.error(module, err);
            }
        }

        public Void visit(LoopStatementNode node) throws CompilerError {
            NodeHandler.accept(this, node.body);
            return null;
        }

        public Void visit(ExpressionStatementNode node) throws CompilerError {
            infer(node.expression);
            return null;
        }

        public Void visit(ReturnStatementNode node) throws CompilerError {
            if (function == null) {
                throw node.error(module, "Return statement cannot be used outside of function body");
            }

            Type value = node.value == null ? context.NULL : infer(node.value);
            if (!isAssignableTo(function.symbol.type.returnType, value)) {
                String err = String.format("Cannot use `%s` value as `%s` value in return from '%s'",
                        value, function.symbol.type.returnType, function.name);
                throw node.error(module, err);
            }
            return null;
        }

        @Override
        public Void visit(BinaryOperationNode node) throws CompilerError {
            Type left = infer(node.left);
            Type right = infer(node.right);
            boolean argsOk = !left.isError() & !right.isError();
            boolean ok = infer(node).isError();

            if (argsOk && !ok) {
                String err = String.format("Cannot apply operator %s on values of types `%s` and `%s`",
                        node.operator, left, right);
                throw node.error(module, err);
            }

            return null;
        }

        @Override
        public Void visit(CallNode node) throws CompilerError {
            Type calledType = infer(node.function);
            boolean functionOk = !calledType.isError();
            FunctionType function = switch (calledType) {
                case FunctionType functionType
                        -> functionType;

                case ClassType classType
                        -> classType.symbol.constructor.type;

                default -> throw node.function.error(module,
                        String.format("Cannot call value of type `%s`", calledType));
            };

            if (function.parameterTypes.size() != node.arguments.size()) {
                String err = String.format("Expected %d arguments, but got %d",
                        function.parameterTypes.size(), node.arguments.size());
                throw node.error(module, err);
            }

            for (int i = 0; i < function.parameterTypes.size(); i++) {
                Type argument = infer(node.arguments.get(i));
                if (argument.isError()) {
                    continue;
                }

                Type parameter = function.parameterTypes.get(i);
                if (!isAssignableTo(parameter, argument)) {
                    String err = String.format("Expected `%s` value, but got `%s` value", parameter, argument);
                    throw node.arguments.get(i).error(module, err);
                }
            }

            return null;
        }

        private boolean isAssignableTo(Type target, Type value) {
            if (target == value) {
                return true;
            }

            if (target instanceof NullableType targetNullableType) {
                if (value == context.NULL) {
                    return true;
                }

                if (value instanceof NullableType valueNullableType) {
                    return isAssignableTo(targetNullableType.type, valueNullableType.type);
                } else {
                    return isAssignableTo(targetNullableType.type, value);
                }
            }

            return false;
        }
    }
}
