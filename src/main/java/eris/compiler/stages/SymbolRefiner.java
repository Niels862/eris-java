package eris.compiler.stages;

import eris.compiler.CompilerError;
import eris.compiler.ast.*;
import eris.compiler.refinement.IsNotNullRefinement;
import eris.compiler.refinement.IsNullRefinement;
import eris.compiler.refinement.Refinement;
import eris.compiler.symbol.ValueSymbol;
import eris.compiler.type.Type;

import java.util.HashMap;
import java.util.Map;

public class SymbolRefiner {
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
    private ValueSymbol isDirectionalNullCheck(ExpressionNode left, ExpressionNode right) {
        if (left instanceof IdentifierNode identifierNode && right instanceof NullLiteralNode) {
            if (identifierNode.symbol instanceof ValueSymbol valueSymbol) {
                return valueSymbol;
            }
        }
        return null;
    }

    private ValueSymbol isNullCheck(ExpressionNode left, ExpressionNode right) {
        ValueSymbol valueSymbol = isDirectionalNullCheck(left, right);
        if (valueSymbol != null) {
            return valueSymbol;
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
        public Map<ValueSymbol, Refinement> visit(BinaryOperationNode node) throws CompilerError {
            if (node.operator.equals("===")) {
                return visitEquals(node.left, node.right);
            }

            if (node.operator.equals("!==")) {
                return negative.visitEquals(node.left, node.right);
            }

            return empty();
        }

        public Map<ValueSymbol, Refinement> visitEquals(ExpressionNode left, ExpressionNode right) {
            ValueSymbol nullCheckedValue = isNullCheck(left, right);
            if (nullCheckedValue != null) {
                return singleton(nullCheckedValue, new IsNullRefinement());
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
        public Map<ValueSymbol, Refinement> defaultHandler(Node node) throws CompilerError {
            return empty();
        }

        public Map<ValueSymbol, Refinement> visitEquals(ExpressionNode left, ExpressionNode right) {
            ValueSymbol nullCheckedValue = isNullCheck(left, right);
            if (nullCheckedValue != null) {
                return singleton(nullCheckedValue, new IsNotNullRefinement());
            }

            return empty();
        }
    }
}
