package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.CompilerError;
import eris.compiler.ir.*;
import eris.module.Function;
import eris.module.Instruction;
import eris.module.OpCode;
import eris.module.constant.Constant;
import eris.module.constant.FunctionReferenceConstant;

import java.util.ArrayList;
import java.util.List;

public class FunctionGenerator {
    private final BuildFunction function;
    private final ConstantManager constants;

    private final List<Instruction> code = new ArrayList<>();

    public FunctionGenerator(BuildFunction function, ConstantManager constants) {
        this.function = function;
        this.constants = constants;
    }

    public Function compile() {
        InstructionEmitter emitter = new InstructionEmitter();

        for (IntermediateInstruction instruction : function.block.instructions) {
            emitter.emit(instruction);
        }

        // FIXME: numArgs hardcoded to one
        return new Function(function.symbol.name, makeCodeArray(), 0);
    }

    private Instruction[] makeCodeArray() {
        Instruction[] codeArray = new Instruction[code.size()];
        code.toArray(codeArray);
        return codeArray;
    }

    private class InstructionEmitter extends IntermediateInstructionVisitor<Void> {
        public void emit(IntermediateInstruction instruction) {
            try {
                instruction.accept(this);
            } catch (CompilerError error) {
                throw new RuntimeException("Unexpected compiler error", error);
            }
        }

        @Override
        public Void visit(LoadConstant instruction) {
            Constant constant;
            if (instruction.constant instanceof Integer integer) {
                constant = constants.getIntegerConstant(integer);
            } else {
                throw new RuntimeException("Unexpected constant type: " + instruction.constant);
            }

            emit(OpCode.LOAD_CONST, constants.getIndexOf(constant));
            return null;
        }

        @Override
        public Void visit(Call instruction) throws CompilerError {
            FunctionReferenceConstant constant = constants.getFunctionReferenceConstant(instruction.function);
            emit(OpCode.CALL, constants.getIndexOf(constant));
            return null;
        }

        @Override
        public Void visit(Return instruction) throws CompilerError {
            emit(OpCode.RETURN);
            return null;
        }

        @Override
        public Void visit(Halt instruction) throws CompilerError {
            emit(OpCode.HALT);
            return null;
        }

        private void emit(OpCode opcode) {
            code.add(new Instruction(opcode, 0));
        }

        private void emit(OpCode opcode, int argument) {
            code.add(new Instruction(opcode, argument));
        }
    }
}
