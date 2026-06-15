package eris.module;

public class Function {
    public final String name;
    public final byte[] code;

    public Function(String name, byte[] code) {
        this.name = name;
        this.code = code;
    }

    public String toString() {
        return "Function " + name;
    }
}
