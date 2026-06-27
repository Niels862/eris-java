package eris.compiler;

import eris.compiler.ast.Node;
import eris.compiler.ir.IntermediateBlock;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.Symbol;
import eris.compiler.symbol.VariableSymbol;

import java.util.List;

public class BuildFunction {
    public final Node node;
    public final FunctionSymbol symbol;
    public final IntermediateBlock block;

    public final List<VariableSymbol> parameters;
    public final List<VariableSymbol> locals;

    public BuildFunction(
            Node node,
            FunctionSymbol symbol,
            IntermediateBlock block,
            List<VariableSymbol> parameters,
            List<VariableSymbol> locals) {
        this.node = node;
        this.symbol = symbol;
        this.block = block;
        this.parameters = parameters;
        this.locals = locals;
    }

    public void dump() {
        System.out.println(this.toString() + " : " + symbol);
        block.dump();
        System.out.println();
    }

    public String toString() {
        return String.format("<BuildFunction %s>", symbol.name);
    }
}
