package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.AttributeValueSymbol;

public class LoadAttribute extends IntermediateInstruction {
    public final AttributeValueSymbol symbol;

    public LoadAttribute(AttributeValueSymbol symbol) {
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
