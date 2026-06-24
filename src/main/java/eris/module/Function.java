package eris.module;

public class Function {
    public final String name;
    public final Instruction[] code;
    public final int numArgs;

    public Function(String name, Instruction[] code, int numArgs) {
        this.name = name;
        this.code = code;
        this.numArgs = numArgs;
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
