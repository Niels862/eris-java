package eris.compiler.ir;

import eris.compiler.CompilerError;

import java.lang.classfile.instruction.SwitchCase;

public abstract class IntermediateInstructionVisitor<T> {
    public T defaultHandler(IntermediateInstruction instruction) throws CompilerError {
        String string = String.format("%s does not implement %s", getClass().getSimpleName(), instruction.getClass().getSimpleName());
        throw new UnsupportedOperationException(string);
    }

    public T visit(LoadConstant instruction) throws CompilerError {
        return defaultHandler(instruction);
    }

    public T visit(LoadLocal instruction) throws CompilerError {
        return defaultHandler(instruction);
    }

    public T visit(StoreLocal instruction) throws CompilerError {
        return defaultHandler(instruction);
    }

    public T visit(Pop instruction) throws CompilerError {
        return defaultHandler(instruction);
    }

    public T visit(Call instruction) throws CompilerError {
        return defaultHandler(instruction);
    }

    public T visit(Return instruction) throws CompilerError {
        return defaultHandler(instruction);
    }

    public T visit(Halt instruction) throws CompilerError {
        return defaultHandler(instruction);
    }
}
