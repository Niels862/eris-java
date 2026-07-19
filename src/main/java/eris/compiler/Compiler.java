package eris.compiler;

import eris.module.Module;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    private final String entry;

    public Compiler(String entry) {
        this.entry = entry;
    }

    public List<Module> compile() {
        List<Path> sourcePaths = new ArrayList<>();
        sourcePaths.add(FileSystems.getDefault().getPath("input"));

        BuildManager manager = new BuildManager(sourcePaths);

        List<Module> compiledModules = new ArrayList<>();
        try {
            BuildModule buildModule = manager.getBuildModule(this.entry);
            buildModule.parse();
            buildModule.analyze();
            Module module = buildModule.compile();

            compiledModules.add(module);
        } catch (CompilerError e) {
            System.err.println(e.getMessage());
            return null;
        }

        return compiledModules;
    }
}
