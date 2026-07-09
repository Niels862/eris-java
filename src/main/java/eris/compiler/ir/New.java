package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.ClassSymbol;

public class New extends IntermediateInstruction {
    public final ClassSymbol symbol;

    public New(ClassSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "NEW " + symbol.name;
    }
}
