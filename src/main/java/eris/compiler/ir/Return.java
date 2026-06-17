package eris.compiler.ir;

import eris.compiler.CompilerError;

public class Return extends IntermediateInstruction {
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "RETURN";
    }
}
