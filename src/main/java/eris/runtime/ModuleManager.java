package eris.runtime;

import eris.module.Module;

import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
    private final Map<String, LoadedModule> loadedModules = new HashMap<>();

    public LoadedModule addModule(Module module) {
        assert !loadedModules.containsKey(module.name);
        LoadedModule loadedModule = new LoadedModule(module, this);
        loadedModules.put(module.name, loadedModule);
        return loadedModule;
    }

    public LoadedModule getModuleByName(String name) {
        LoadedModule loadedModule = loadedModules.get(name);
        if (loadedModule != null) {
            return loadedModule;
        }

        throw new RuntimeException("Module with name " + name + " not found");
    }
}
