package eris.compiler;

import eris.compiler.ast.Node;
import eris.compiler.ir.IntermediateBlock;
import eris.compiler.symbol.Symbol;

public class BuildFunction {
    public final Node node;
    public final Symbol symbol;
    public final IntermediateBlock block;

    public BuildFunction(Node node, Symbol symbol, IntermediateBlock block) {
        this.node = node;
        this.symbol = symbol;
        this.block = block;
    }

    public void dump() {
        System.out.println(this.toString() + " : " + symbol);
        block.dump();
    }

    public String toString() {
        return String.format("<BuildFunction %s>", symbol.name);
    }
}
