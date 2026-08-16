package eris.compiler.stages;

import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.symbol.ClassSymbol;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.Symbol;
import eris.compiler.symbol.ValueSymbol;
import eris.compiler.type.*;

import javax.print.attribute.standard.PresentationDirection;

public class TypeInferrer {
    private final NodeHandler handler = new NodeHandler();
    private final boolean resolveInferenceTypes;

    public TypeInferrer(boolean resolveInferenceTypes) {
        this.resolveInferenceTypes = resolveInferenceTypes;
    }

    public Type infer(ExpressionNode node) {
        return handler.infer(node);
    }

    private class NodeHandler extends NodeVisitor<Type> {
        private final TypeContext context = TypeContext.instance;

        public Type infer(ExpressionNode node) {
            try {
                Type type = inferType(node);
                if (type instanceof InferenceType) {
                    System.out.printf("%s on %s (%b)%n", type, node, resolveInferenceTypes);
                }
                return type;
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
                return visitSymbol(node.symbol);
            }

            if (resolveInferenceTypes) {
                Type object = infer(node.object);

                if (object instanceof ClassValueType classValueType) {
                    ClassSymbol classSymbol = classValueType.symbol;
                    Symbol symbol = classSymbol.symbolTable.lookup(node.member);

                    if (symbol != null) {
                        node.symbol = symbol;
                        return visitSymbol(symbol);
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
            return visitSymbol(node.symbol);
        }

        private Type visitSymbol(Symbol symbol) {
            if (symbol instanceof ValueSymbol valueSymbol) {
                Type type = valueSymbol.type;
                if (resolveInferenceTypes && type instanceof InferenceType inferenceType) {
                    type = infer(inferenceType.expression);
                    assert !(type instanceof InferenceType);
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
