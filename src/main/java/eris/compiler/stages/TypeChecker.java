package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.refinement.IsInstanceRefinement;
import eris.compiler.refinement.Refinement;
import eris.compiler.symbol.*;
import eris.compiler.type.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeChecker {
    private final BuildModule module;
    private final NodeHandler handler = new NodeHandler();
    private final TargetNodeHandler targetHandler = new TargetNodeHandler();

    private final List<Map<ValueSymbol, Refinement>> scopedRefinements = new ArrayList<>();
    private final TypeInferrer inferrer = new TypeInferrer(true, scopedRefinements);
    private final SymbolRefiner refiner = new SymbolRefiner();
    private final TypeContext context = TypeContext.instance;

    private FunctionNode function;

    public TypeChecker(BuildModule module) {
        this.module = module;
    }

    public void check() throws CompilerError {
        module.moduleNode.accept(handler);
    }

    private class NodeHandler extends NodeVisitor<Void> {
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
            withScope(node.body);
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
            Type target = targetHandler.infer(node.target);
            if (!isAssignableTo(target, value)) {
                throw node.error(module, String.format("Cannot assign `%s` value to `%s` target", value, target));
            }

            if (node.target instanceof IdentifierNode identifierTarget) {
                if (identifierTarget.symbol instanceof LocalValueSymbol localValueSymbolTarget) {
                    scopedRefinements.getLast().put(localValueSymbolTarget, new IsInstanceRefinement(value));
                }
            }

            return null;
        }

        public Void visit(IfElseStatementNode node) throws CompilerError {
            visitCondition(node.condition);
            withScope(node.thenBody, refiner.refineIfTrue(node.condition));
            withScope(node.elseBody, refiner.refineIfFalse(node.condition));
            return null;
        }

        public Void visit(WhileStatementNode node) throws CompilerError {
            visitCondition(node.condition);
            withScope(node.body, refiner.refineIfTrue(node.condition));
            return null;
        }

        public Void visit(DoWhileStatementNode node) throws CompilerError {
            visitCondition(node.condition);
            withScope(node.body);
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
            withScope(node.body);
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

        private Map<ValueSymbol, Refinement> withScope(
                List<StatementNode> statements,
                Map<ValueSymbol, Refinement> refinements) throws CompilerError {
            scopedRefinements.add(refinements);
            NodeVisitor.accept(this, statements);
            scopedRefinements.removeLast();
            return refinements;
        }

        private Map<ValueSymbol, Refinement> withScope(List<StatementNode> statements) throws CompilerError {
            return withScope(statements, refiner.empty());
        }
    }

    private class TargetNodeHandler extends NodeVisitor<Type> {
        private Type infer(ExpressionNode node) throws CompilerError {
            assert !node.hasInferredType();
            Type type = node.accept(this);
            node.setInferredType(type);
            return type;
        }

        @Override
        public Type defaultHandler(Node node) throws CompilerError {
            throw node.error(module, "Invalid assignment target");
        }

        @Override
        public Type visit(MemberNode node) throws CompilerError {
            handler.infer(node.object);
            return inferrer.infer(node);
        }

        @Override
        public Type visit(IdentifierNode node) {
            return inferrer.infer(node.symbol);
        }
    }
}
