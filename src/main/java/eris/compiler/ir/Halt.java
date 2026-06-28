package eris.compiler.ir;

import eris.compiler.CompilerError;

public class Halt extends TerminatorInstruction {
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "HALT";
    }
}
