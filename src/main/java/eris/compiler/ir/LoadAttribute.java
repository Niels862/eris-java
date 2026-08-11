package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.ValueSymbol;

public class LoadAttribute extends IntermediateInstruction {
    public final ValueSymbol symbol;

    public LoadAttribute(ValueSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LOAD_ATTRIBUTE " + symbol;
    }
}
