package eris.compiler;

import eris.module.Module;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildManager {
    private final List<Path> sourcePaths;
    private final Map<String, BuildModule> modules = new HashMap<>();

    public BuildManager(List<Path> sourcePaths) {
        this.sourcePaths = sourcePaths;
    }

    public BuildModule getBuildModule(String name) throws CompilerError {
        if (modules.containsKey(name)) {
            return modules.get(name);
        }

        String erisFileName = moduleNameToErisPath(name);

        for (Path sourcePath : sourcePaths) {
            Path path = sourcePath.resolve(erisFileName);

            if (Files.exists(path) && Files.isRegularFile(path)) {
                BuildModule module = new BuildModule(name, path);

                modules.put(name, module);
                return module;
            }
        }

        throw new CompilerError(String.format("Could not find name '%s' in source paths", name));
    }

    public List<Module> getCompiledDependencyModules(BuildModule entryModule) throws CompilerError {
        List<Module> compiledModules = new ArrayList<>();

        for (BuildModule module : modules.values()) {
            if (!module.equals(entryModule)) {
                compiledModules.add(module.compile());
            }
        }

        return compiledModules;
    }

    private String moduleNameToErisPath(String moduleName) {
        return moduleName + ".eris";
    }
}
