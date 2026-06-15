package eris.module.constant;

public class FunctionConstant extends Constant {
    public final String name;
    public final byte[] code;

    public FunctionConstant(String name, byte[] code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return "Function " + name;
    }
}
