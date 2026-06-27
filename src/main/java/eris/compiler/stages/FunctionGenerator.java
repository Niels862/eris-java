package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.CompilerError;
import eris.compiler.ir.*;
import eris.compiler.symbol.VariableSymbol;
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
        int slotIndex = 0;
        for (VariableSymbol parameter : function.parameters) {
            parameter.setSlotIndex(slotIndex++);
        }
        for (VariableSymbol local : function.locals) {
            local.setSlotIndex(slotIndex++);
        }

        InstructionEmitter emitter = new InstructionEmitter();
        for (IntermediateInstruction instruction : function.block.instructions) {
            emitter.emit(instruction);
        }

        return new Function(function.symbol.name, makeCodeArray(), function.parameters.size(), function.locals.size());
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
        public Void visit(LoadLocal instruction) {
            emit(OpCode.LOAD_LOCAL, instruction.symbol.getSlotIndex());
            return null;
        }

        @Override
        public Void visit(StoreLocal instruction) {
            emit(OpCode.STORE_LOCAL, instruction.symbol.getSlotIndex());
            return null;
        }

        @Override
        public Void visit(Pop instruction) {
            emit(OpCode.POP);
            return null;
        }

        @Override
        public Void visit(Call instruction) {
            FunctionReferenceConstant constant = constants.getFunctionReferenceConstant(instruction.function);
            emit(OpCode.CALL, constants.getIndexOf(constant));
            return null;
        }

        @Override
        public Void visit(Return instruction) {
            emit(OpCode.RETURN);
            return null;
        }

        @Override
        public Void visit(Halt instruction) {
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
