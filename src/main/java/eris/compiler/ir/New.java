package eris.compiler.ir;

import eris.compiler.CompilerError;
import eris.compiler.symbol.ClassSymbol;

public class New extends IntermediateInstruction {
    public final ClassSymbol clazz;

    public New(ClassSymbol clazz) {
        this.clazz = clazz;
    }

    @Override
    public <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "NEW " + clazz.name;
    }
}
