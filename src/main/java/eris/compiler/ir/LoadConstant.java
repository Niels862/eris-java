package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.ast.NodeVisitor;
import eris.module.constant.Constant;

public class LoadConstant extends IntermediateInstruction {
    public final Constant constant;

    public LoadConstant(Constant constant) {
        this.constant = constant;
    }

    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "LOAD_CONSTANT " + constant;
    }
}
