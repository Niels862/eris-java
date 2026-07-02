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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionGenerator {
    private final BuildFunction function;
    private final ConstantManager constants;

    private final List<Instruction> code = new ArrayList<>();

    private BasicBlock nextBlock;

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

        // First BLOCK is entry block. Last BLOCK is exit block which should not be referenced after semantic analysis
        List<BasicBlock> blocks = function.blocks;
        Map<Integer, Integer> blockOffsets = new HashMap<>();

        InstructionEmitter emitter = new InstructionEmitter();
        for (int i = 0; i < blocks.size() - 1; i++) {
            BasicBlock block = blocks.get(i);
            blockOffsets.put(block.id, code.size());
            nextBlock = blocks.get(i + 1);

            for (IntermediateInstruction instruction : block.instructions) {
                emitter.emit(instruction);
            }
        }

        for (int i = 0; i < code.size(); i++) {
            Instruction instruction = code.get(i);
            if (instruction.opcode.format == OpCode.Format.JUMP_TARGET) {
                int jumpOffset = blockOffsets.get(instruction.argument) - i - 1;
                code.set(i, new Instruction(instruction.opcode, jumpOffset));
            }
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
            Constant constant = switch (instruction.constant) {
                case Integer integerConstant
                        -> constants.getIntegerConstant(integerConstant);

                case Boolean booleanConstant
                        -> constants.getIntegerConstant(booleanConstant ? 1 : 0);

                case String stringConstant
                        -> constants.getStringConstant(stringConstant);

                case null, default
                        -> throw new RuntimeException("Unexpected constant type: " + instruction.constant);
            };

            emit(OpCode.LOAD_CONST, constants.getIndexOf(constant));
            return null;
        }

        @Override
        public Void visit(LoadNull instruction) throws CompilerError {
            emit(OpCode.LOAD_NULL);
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
        public Void visit(BinaryOperation instruction) {
            switch (instruction.operator) {
                case "==="
                        -> emit(OpCode.EQ);

                case "!=="
                        -> emit(OpCode.NE);

                default
                        -> throw new RuntimeException("Unexpected operator: " + instruction.operator);
            }
            return null;
        }

        @Override
        public Void visit(Convert instruction) {
            assert instruction.toType != null;
            return null;
        }

        @Override
        public Void visit(Jump instruction) {
            if (instruction.out != nextBlock) {
                emit(OpCode.JUMP, instruction.out.id);
            }
            return null;
        }

        @Override
        public Void visit(Branch instruction) {
            if (instruction.thenOut == nextBlock) {
                emit(OpCode.BRANCH_IF_FALSE, instruction.elseOut.id);
            } else {
                emit(OpCode.BRANCH_IF_TRUE, instruction.thenOut.id);
                if (instruction.elseOut != nextBlock) {
                    emit(OpCode.JUMP, instruction.elseOut.id);
                }
            }
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
