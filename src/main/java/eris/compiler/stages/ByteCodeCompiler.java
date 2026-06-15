package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.module.Function;

public class ByteCodeCompiler {
    private final BuildFunction function;
    private final ConstantManager constants;

    public ByteCodeCompiler(BuildFunction function, ConstantManager constants) {
        this.function = function;
        this.constants = constants;
    }

    public Function compile() {
        byte[] code = new byte[0];
        return new Function(function.symbol.name, code);
    }
}
