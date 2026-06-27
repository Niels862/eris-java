package eris.module;

public class Function {
    public final String name;
    public final Instruction[] code;
    public final int numArgs;
    public final int numLocals;

    public Function(String name, Instruction[] code, int numArgs, int numLocals) {
        this.name = name;
        this.code = code;
        this.numArgs = numArgs;
        this.numLocals = numLocals;
    }

    public void dump() {
        System.out.println("Function " + name);
        for (Instruction instruction : code) {
            System.out.println("  " + instruction);
        }
    }

    public String toString() {
        return "Function " + name;
    }
}
