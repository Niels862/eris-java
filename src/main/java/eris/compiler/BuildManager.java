package eris.compiler;

import java.nio.file.Files;
import java.nio.file.Path;
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

        String erisName = moduleNameToErisPath(name);

        for (Path sourcePath : sourcePaths) {
            Path path = sourcePath.resolve(erisName);

            if (Files.exists(path) && Files.isRegularFile(path)) {
                BuildModule module = new BuildModule(path);

                modules.put(erisName, module);
                return module;
            }
        }

        throw new CompilerError(String.format("Could not find name '%s' in source paths", name));
    }

    private String moduleNameToErisPath(String moduleName) {
        return moduleName + ".eris";
    }
}
