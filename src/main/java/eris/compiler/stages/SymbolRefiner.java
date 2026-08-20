package eris.compiler.stages;

import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ast.*;
import eris.compiler.Refinement;
import eris.compiler.symbol.ValueSymbol;
import eris.compiler.type.Type;

import java.util.HashMap;
import java.util.Map;

public class SymbolRefiner {
    private final TypeContext context = TypeContext.instance;
    private final PositiveNodeHandler positive = new PositiveNodeHandler();
    private final NegativeNodeHandler negative = new NegativeNodeHandler();

    public Map<ValueSymbol, Refinement> refineIfTrue(ExpressionNode node) {
        return positive.refine(node);
    }

    public Map<ValueSymbol, Refinement> refineIfFalse(ExpressionNode node) {
        return negative.refine(node);
    }

    public Map<ValueSymbol, Refinement> empty() {
        return new HashMap<>();
    }

    public Map<ValueSymbol, Refinement> singleton(ValueSymbol symbol, Refinement refinement) {
        Map<ValueSymbol, Refinement> map = empty();
        map.put(symbol, refinement);
        return map;
    }

    // left is value, right is null
    private SymbolValue isDirectionalNullCheck(ExpressionNode left, ExpressionNode right) {
        if (left instanceof IdentifierNode identifierNode && right instanceof NullLiteralNode) {
            if (identifierNode.symbol instanceof ValueSymbol valueSymbol) {
                return new SymbolValue(valueSymbol, left.getInferredType());
            }
        }
        return null;
    }

    private SymbolValue isNullCheck(ExpressionNode left, ExpressionNode right) {
        SymbolValue value = isDirectionalNullCheck(left, right);
        if (value != null) {
            return value;
        }
        return isDirectionalNullCheck(right, left);
    }

    private class PositiveNodeHandler extends NodeVisitor<Map<ValueSymbol, Refinement>> {
        public Map<ValueSymbol, Refinement> refine(ExpressionNode node) {
            try {
                return node.accept(this);
            } catch (CompilerError e) {
                throw new RuntimeException("Unexpected error", e);
            }
        }

        @Override
        public Map<ValueSymbol, Refinement> defaultHandler(Node node) {
            return empty();
        }

        @Override
        public Map<ValueSymbol, Refinement> visit(BinaryOperationNode node) {
            if (node.operator.equals("===")) {
                return visitEquals(node.left, node.right);
            }

            if (node.operator.equals("!==")) {
                return negative.visitEquals(node.left, node.right);
            }

            return empty();
        }

        public Map<ValueSymbol, Refinement> visitEquals(ExpressionNode left, ExpressionNode right) {
            SymbolValue value = isNullCheck(left, right);
            if (value != null) {
                return singleton(value.symbol, new Refinement(context.NULL));
            }

            return empty();
        }
    }

    private class NegativeNodeHandler extends NodeVisitor<Map<ValueSymbol, Refinement>> {
        public Map<ValueSymbol, Refinement> refine(ExpressionNode node) {
            try {
                return node.accept(this);
            } catch (CompilerError e) {
                throw new RuntimeException("Unexpected error", e);
            }
        }

        @Override
        public Map<ValueSymbol, Refinement> defaultHandler(Node node) {
            return empty();
        }

        public Map<ValueSymbol, Refinement> visitEquals(ExpressionNode left, ExpressionNode right) {
            SymbolValue value = isNullCheck(left, right);
            if (value != null) {
                return singleton(value.symbol, new Refinement(value.type.asNonNull()));
            }

            return empty();
        }
    }

    private static class SymbolValue {
        public final ValueSymbol symbol;
        public final Type type;

        public SymbolValue(ValueSymbol symbol, Type type) {
            this.symbol = symbol;
            this.type = type;
        }
    }
}
