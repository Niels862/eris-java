package eris.module;

public class Function {
    public final String name;
    public final Instruction[] code;

    public Function(String name, Instruction[] code) {
        this.name = name;
        this.code = code;
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
