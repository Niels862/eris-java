package eris.compiler;

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

        try {
            BuildModule module = manager.getBuildModule(this.entry);
            module.parse(manager);

            System.out.println(module.toString());
        } catch (CompilerError e) {
            System.err.println(e.getMessage());
        }

        return new ArrayList<>();
    }
}
