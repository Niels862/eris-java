package eris.compiler.type;

import java.util.List;

public class FunctionType extends Type {
    public final List<Type> parameterTypes;
    public final Type returnType;

    public FunctionType(List<Type> parameterTypes, Type returnType) {
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameterTypes.get(i).toString());
        }
        sb.append(") -> ");
        sb.append(returnType.toString());

        return sb.toString();
    }
}
