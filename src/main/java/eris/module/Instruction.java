package eris.module;

public class Instruction {
    public final OpCode opcode;
    public final int argument;

    public Instruction(OpCode opcode, int argument) {
        this.opcode = opcode;
        this.argument = argument;
    }

    public String toString() {
        return opcode.toString() + " " + argument;
    }
}
