package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.VariableSymbol;

public class StoreLocal extends IntermediateInstruction {
    public final VariableSymbol symbol;
    public final boolean initialAssignment;

    public StoreLocal(VariableSymbol symbol, boolean initialAssignment) {
        this.symbol = symbol;
        this.initialAssignment = initialAssignment;
    }

    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "STORE_LOCAL " + symbol.name;
    }
}
