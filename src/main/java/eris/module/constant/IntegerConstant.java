package eris.module.constant;

public class IntegerConstant extends Constant {
    public final int value;

    public IntegerConstant(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
