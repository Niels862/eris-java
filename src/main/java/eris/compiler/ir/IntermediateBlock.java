package eris.compiler.ir;

import java.util.ArrayList;
import java.util.List;

public class IntermediateBlock {
    public final int id;
    public final List<IntermediateInstruction>  instructions = new ArrayList<>();

    public IntermediateBlock(int id) {
        this.id = id;
    }

    public void dump() {
        for (IntermediateInstruction instruction : instructions) {
            System.out.println("  " + instruction);
        }
    }
}
