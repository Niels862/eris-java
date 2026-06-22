package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.module.constant.Constant;
import eris.module.constant.FunctionReferenceConstant;

public class Call extends IntermediateInstruction {
    public final FunctionReferenceConstant reference;

    public Call(FunctionReferenceConstant reference) {
        this.reference = reference;
    }

    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "CALL " + reference;
    }
}
