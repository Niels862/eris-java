package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.type.Type;

public class Convert extends IntermediateInstruction {
    public Type fromType;
    public Type toType;

    public Convert() {}

    public Convert(Type toType) {
        this.toType = toType;
    }

    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (toType == null) {
            return "CONVERT";
        }
        if (fromType == null) {
            return "CONVERT -> " + toType;
        }
        return "CONVERT " + fromType + " -> " + toType;
    }
}
