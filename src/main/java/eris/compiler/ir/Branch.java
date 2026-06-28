package eris.compiler.ir;

import eris.compiler.CompilerError;

public class Branch extends TerminatorInstruction {
    public final BasicBlock thenBranch;
    public final BasicBlock elseBranch;

    public Branch(BasicBlock thenBranch, BasicBlock elseBranch) {
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "BRANCH " + thenBranch + " : " + elseBranch;
    }
}
