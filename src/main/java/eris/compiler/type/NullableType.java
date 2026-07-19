package eris.compiler.type;

public class NullableType extends ValueType {
    public final Type type;

    public NullableType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        if (type == null) {
            return "null";
        }

        String inner;
        if (type instanceof ClassValueType classValueType) {
            inner = classValueType.toString();
        } else {
            inner = "(" + type.toString() + ")";
        }
        return inner + "?";
    }
}
