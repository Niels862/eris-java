package eris.compiler.ir;

import eris.compiler.CompilerError;

public class LoadNull extends IntermediateInstruction {
    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LOAD_NULL";
    }
}
