package eris.runtime;

import eris.module.Class;

public class LoadedClass {
    public final LoadedModule module;
    public final Class clazz;

    public LoadedClass(LoadedModule module, Class clazz) {
        this.module = module;
        this.clazz = clazz;
    }
}
