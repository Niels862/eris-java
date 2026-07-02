package eris.compiler.type;

public class NullableType extends Type {
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
        if (type instanceof ClassType classType) {
            inner = classType.toString();
        } else {
            inner = "(" + type.toString() + ")";
        }
        return inner + "?";
    }
}
