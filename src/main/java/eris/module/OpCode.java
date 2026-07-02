package eris.module;

public enum OpCode {
    LOAD_CONST(Format.INTEGER),
    LOAD_NULL,
    LOAD_LOCAL(Format.INTEGER),
    STORE_LOCAL(Format.INTEGER),
    POP,

    EQ,
    NE,

    JUMP(Format.JUMP_TARGET),
    BRANCH_IF_TRUE(Format.JUMP_TARGET),
    BRANCH_IF_FALSE(Format.JUMP_TARGET),

    CALL(Format.INTEGER),
    RETURN,

    HALT;

    public final Format format;

    OpCode() {
        this.format = Format.NO_ARGUMENT;
    }

    OpCode(Format format) {
        this.format = format;
    }

    public enum Format {
        NO_ARGUMENT,
        INTEGER,
        JUMP_TARGET,
    }
}
