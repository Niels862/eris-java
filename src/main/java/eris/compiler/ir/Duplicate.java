package eris.compiler.ir;

import eris.compiler.CompilerError;

public class Duplicate extends IntermediateInstruction {
    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "DUPLICATE";
    }
}
