package eris.runtime;

import eris.module.Function;

public class LoadedFunction {
    public LoadedModule module;
    public Function function;
    public NativeFunction nativeFunction;

    public LoadedFunction(LoadedModule module, Function function, NativeFunction nativeFunction) {
        this.module = module;
        this.function = function;
        this.nativeFunction = nativeFunction;
    }
}
