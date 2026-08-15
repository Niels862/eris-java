package eris.compiler.stages;

import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.symbol.ClassSymbol;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.Symbol;
import eris.compiler.symbol.ValueSymbol;
import eris.compiler.type.ClassType;
import eris.compiler.type.FunctionType;
import eris.compiler.type.Type;

public class TypeInferrer {
    private final NodeHandler handler = new NodeHandler();

    public Type infer(ExpressionNode node) {
        return handler.infer(node);
    }

    private static class NodeHandler extends NodeVisitor<Type> {
        private final TypeContext context = TypeContext.instance;

        public Type infer(ExpressionNode node) {
            try {
                if (node.hasInferredType()) {
                    return node.getInferredType();
                } else {
                    return node.accept(this);
                }
            } catch (CompilerError e) {
                throw new RuntimeException("Unexpected error", e);
            }
        }

        @Override
        public Type defaultHandler(Node node) {
            return null;
        }

        @Override
        public Type visit(BinaryOperationNode node) {
            return null;
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

            throw new UnsupportedOperationException();
        }

        @Override
        public Type visit(MemberNode node) {
            return visitSymbol(node.symbol);
        }

        @Override
        public Type visit(IdentifierNode node) {
            return visitSymbol(node.symbol);
        }

        private Type visitSymbol(Symbol symbol) {
            if (symbol instanceof ValueSymbol valueSymbol) {
                return valueSymbol.type;
            }

            if (symbol instanceof FunctionSymbol functionSymbol) {
                return functionSymbol.type;
            }

            if (symbol instanceof ClassSymbol classSymbol) {
                return classSymbol.classType;
            }

            throw new UnsupportedOperationException();
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
