package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.symbol.*;
import eris.compiler.type.*;

public class TypeChecker extends NodeVisitor<Void> {
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

            return false;
        }
    }
}
