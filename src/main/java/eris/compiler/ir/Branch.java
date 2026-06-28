package eris.compiler.ir;

import eris.compiler.CompilerError;

public class Branch extends TerminatorInstruction {
    public final BasicBlock thenOut;
    public final BasicBlock elseOut;

    public Branch(BasicBlock thenOut, BasicBlock elseOut) {
        this.thenOut = thenOut;
        this.elseOut = elseOut;
    }

    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "BRANCH " + thenOut + " : " + elseOut;
    }
}
