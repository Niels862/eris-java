package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.VariableSymbol;

public class LoadLocal extends IntermediateInstruction {
    public final VariableSymbol symbol;

    public LoadLocal(VariableSymbol symbol) {
        this.symbol = symbol;
    }

    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "LOAD_LOCAL " + symbol.name;
    }
}
