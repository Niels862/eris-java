package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.VariableSymbol;

public class StoreLocal extends IntermediateInstruction {
    public final VariableSymbol symbol;

    public StoreLocal(VariableSymbol symbol) {
        this.symbol = symbol;
    }

    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "STORE_LOCAL " + symbol.name;
    }
}
