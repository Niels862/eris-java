package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.CompilerError;
import eris.compiler.ir.IntermediateInstruction;
import eris.compiler.ir.IntermediateInstructionVisitor;
import eris.compiler.ir.LoadConstant;
import eris.compiler.ir.Return;
import eris.module.Function;
import eris.module.Instruction;
import eris.module.OpCode;

import java.util.ArrayList;
import java.util.List;

public class FunctionCompiler {
    private final BuildFunction function;
    private final ConstantManager constants;

    private final List<Instruction> code = new ArrayList<>();

    public FunctionCompiler(BuildFunction function, ConstantManager constants) {
        this.function = function;
        this.constants = constants;
    }

    public Function compile() {
        InstructionEmitter emitter = new InstructionEmitter();

        for (IntermediateInstruction instruction : function.block.instructions) {
            emitter.emit(instruction);
        }

        return new Function(function.symbol.name, makeCodeArray());
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
            emit(OpCode.LOAD_CONST, constants.getIndexOf(instruction.constant));
            return null;
        }

        @Override
        public Void visit(Return instruction) throws CompilerError {
            emit(OpCode.RETURN);
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
