package eris.compiler.ir;

import eris.compiler.CompilerError;

public class BinaryOperation extends IntermediateInstruction {
    public final String operator;

    public BinaryOperation(String operator) {
        this.operator = operator;
    }

    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "BINARY_OPERATION " + operator;
    }
}
