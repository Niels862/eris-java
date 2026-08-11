package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.ValueSymbol;

public class StoreAttribute extends IntermediateInstruction {
    public final ValueSymbol symbol;

    public StoreAttribute(ValueSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "STORE_ATTRIBUTE " + symbol;
    }
}
