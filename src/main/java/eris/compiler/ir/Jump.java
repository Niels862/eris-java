package eris.compiler.ir;

import eris.compiler.CompilerError;

public class Jump extends TerminatorInstruction {
    public final BasicBlock out;

    public Jump(BasicBlock out) {
        this.out = out;
    }

    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "JUMP " + out;
    }
}
