package eris.runtime;

import eris.module.Function;

public class LoadedFunction {
    public LoadedModule module;
    public Function function;

    public LoadedFunction(LoadedModule module, Function function) {
        this.module = module;
        this.function = function;
    }
}
