package eris.compiler.ir;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    public final int id;
    public final List<IntermediateInstruction>  instructions = new ArrayList<>();

    public BasicBlock(int id) {
        this.id = id;
    }

    public void dump() {
        System.out.println(this + ":");
        for (IntermediateInstruction instruction : instructions) {
            System.out.println("  " + instruction);
        }
    }

    public IntermediateInstruction getLast() {
        if (instructions.isEmpty()) {
            return null;
        }
        return instructions.getLast();
    }

    public TerminatorInstruction getTerminator() {
        IntermediateInstruction instruction = getLast();
        assert instruction instanceof TerminatorInstruction;
        return (TerminatorInstruction) instruction;
    }

    public String toString() {
        return "L" + id;
    }
}
