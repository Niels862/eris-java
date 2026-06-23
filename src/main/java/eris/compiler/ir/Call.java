package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.FunctionSymbol;
import eris.module.constant.Constant;
import eris.module.constant.FunctionReferenceConstant;

public class Call extends IntermediateInstruction {
    public final FunctionSymbol function;

    public Call(FunctionSymbol function) {
        this.function = function;
    }

    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    public String toString() {
        return "CALL " + function.name;
    }
}
