package eris.compiler;

import eris.compiler.ast.Node;
import eris.compiler.symbol.Symbol;

public class BuildFunction {
    private final Node node;
    private final Symbol symbol;

    public BuildFunction(Node node, Symbol symbol) {
        this.node = node;
        this.symbol = symbol;
    }

    public String toString() {
        return String.format("<BuildFunction %s>", symbol.name);
    }
}
