package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.ValueSymbol;

public class StoreLocal extends IntermediateInstruction {
    public final ValueSymbol symbol;
    public final boolean isInitializingAssignment;

    public Convert converter;

    public StoreLocal(ValueSymbol symbol, boolean isInitializingAssignment, Convert converter) {
        this.symbol = symbol;
        this.isInitializingAssignment = isInitializingAssignment;
        this.converter = converter;
    }

    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "STORE_LOCAL " + symbol.name;
    }
}
