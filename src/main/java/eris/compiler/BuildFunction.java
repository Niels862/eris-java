package eris.compiler;

import eris.compiler.ast.Node;
import eris.compiler.ir.BasicBlock;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.VariableSymbol;

import java.util.List;

public class BuildFunction {
    public final Node node;
    public final FunctionSymbol symbol;
    public final List<BasicBlock> blocks;

    public final List<VariableSymbol> parameters;
    public final List<VariableSymbol> locals;

    public BuildFunction(
            Node node,
            FunctionSymbol symbol,
            List<BasicBlock> blocks,
            List<VariableSymbol> parameters,
            List<VariableSymbol> locals) {
        this.node = node;
        this.symbol = symbol;
        this.blocks = blocks;
        this.parameters = parameters;
        this.locals = locals;
    }

    public void dump() {
        System.out.printf("%s: %s with parameters: %s, and locals: %s%n", this, symbol, parameters, locals);
        for (BasicBlock block : blocks) {
            block.dump();
        }
        System.out.println();
    }

    public String toString() {
        return String.format("<BuildFunction %s>", symbol.name);
    }
}
