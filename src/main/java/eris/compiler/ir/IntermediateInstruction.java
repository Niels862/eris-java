package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.ast.NodeVisitor;

public abstract class IntermediateInstruction {
    public abstract <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError;

    public abstract String toString();
}
