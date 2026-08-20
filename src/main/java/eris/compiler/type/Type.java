package eris.compiler.type;

import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.module.TypeTag;

public abstract class Type {
    @Override
    public abstract String toString();

    public Type asNonNull() {
        if (this instanceof NullableType nullableType) {
            return nullableType.type;
        }
        return this;
    }

    public boolean isError() {
        return this instanceof ErrorType;
    }

    public TypeTag toTypeTag() {
        TypeContext context = TypeContext.instance;

        if (this == context.INT) {
            return TypeTag.INT;
        }

        return TypeTag.OBJECT;
    }
}
