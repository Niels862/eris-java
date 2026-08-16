package eris.compiler.stages;

import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.refinement.Refinement;
import eris.compiler.symbol.*;
import eris.compiler.type.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TypeInferrer {
    private final static TypeInferrer statelessInferrer = new TypeInferrer(true);

    private final NodeHandler handler = new NodeHandler();
    private final TypeContext context = TypeContext.instance;

    private final boolean resolveInferenceTypes;
    private final List<Map<ValueSymbol, Refinement>> scopedRefinements;

    public TypeInferrer(boolean resolveInferenceTypes) {
        this.resolveInferenceTypes = resolveInferenceTypes;
        this.scopedRefinements = Collections.emptyList();
    }

    public TypeInferrer(boolean resolveInferenceTypes, List<Map<ValueSymbol, Refinement>> scopedRefinements) {
        this.resolveInferenceTypes = resolveInferenceTypes;
        this.scopedRefinements = scopedRefinements;
    }

    public Type infer(ExpressionNode node) {
        return handler.handle(node);
    }

    public Type infer(Symbol symbol) {
        if (symbol instanceof ValueSymbol valueSymbol) {
            Type type = valueSymbol.type;

            if (resolveInferenceTypes && type instanceof InferenceType inferenceType) {
                type = statelessInferrer.infer(inferenceType.expression);
                assert !(type instanceof InferenceType);
                valueSymbol.type = type;
            }

            return type;
        }

        if (symbol instanceof FunctionSymbol functionSymbol) {
            return functionSymbol.type;
        }

        if (symbol instanceof ClassSymbol classSymbol) {
            return classSymbol.classType;
        }

        throw new UnsupportedOperationException("Unsupported symbol: " + symbol);
    }

    private class NodeHandler extends NodeVisitor<Type> {
        public Type handle(ExpressionNode node) {
            try {
                return inferType(node);
            } catch (CompilerError e) {
                throw new RuntimeException("Unexpected error", e);
            }
        }

        private Type inferType(ExpressionNode node) throws CompilerError {
            if (node.hasInferredType()) {
                return node.getInferredType();
            } else {
                return node.accept(this);
            }
        }

        @Override
        public Type visit(BinaryOperationNode node) {
            Type left = infer(node.left);
            Type right = infer(node.right);

            switch (node.operator) {
                case "===", "!==" -> {
                    return context.BOOL;
                }

                default -> {
                    throw new UnsupportedOperationException("Unknown operator: " + node.operator);
                }
            }
        }

        @Override
        public Type visit(CallNode node) {
            Type type = infer(node.function);

            if (type instanceof FunctionType functionType) {
                return functionType.returnType;
            }

            if (type instanceof ClassType classType) {
                return classType.symbol.valueType;
            }

            return context.ERROR;
        }

        @Override
        public Type visit(MemberNode node) {
            if (node.symbol != null) {
                return infer(node.symbol);
            }

            if (resolveInferenceTypes) {
                Type object = infer(node.object);

                if (object instanceof ClassValueType classValueType) {
                    ClassSymbol classSymbol = classValueType.symbol;
                    Symbol symbol = classSymbol.symbolTable.lookup(node.member);

                    if (symbol != null) {
                        node.symbol = symbol;
                        return infer(symbol);
                    } else {
                        return context.ERROR;
                    }
                } else {
                    return context.ERROR;
                }
            } else {
                return new InferenceType(node);
            }
        }

        @Override
        public Type visit(IdentifierNode node) {
            assert node.symbol != null;
            Type type = infer(node.symbol);
            if (node.symbol instanceof ValueSymbol valueSymbol) {
                return applyRefinement(valueSymbol, type);
            } else {
                return type;
            }
        }

        private Type applyRefinement(ValueSymbol symbol, Type type) {
            if (!(symbol instanceof LocalValueSymbol)) {
                return type;
            }

            for (Map<ValueSymbol, Refinement> map : scopedRefinements) {
                Refinement refinement = map.get(symbol);
                if (refinement != null) {
                    type = refinement.apply(type);
                }
            }

            return type;
        }

        public Type visit(IntegerLiteralNode node) {
            return context.INT;
        }

        public Type visit(BooleanLiteralNode node) {
            return context.BOOL;
        }

        public Type visit(StringLiteralNode node) {
            return context.STRING;
        }

        public Type visit(NullLiteralNode node) {
            return context.NULL;
        }
    }
}
