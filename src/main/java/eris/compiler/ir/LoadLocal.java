package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.ValueSymbol;

public class LoadLocal extends IntermediateInstruction {
    public final ValueSymbol symbol;

    public LoadLocal(ValueSymbol symbol) {
        this.symbol = symbol;
    }

    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "LOAD_LOCAL " + symbol.name;
    }
}
