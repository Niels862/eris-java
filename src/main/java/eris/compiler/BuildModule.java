package eris.compiler;

import eris.compiler.modulestate.*;

import java.nio.file.Path;

public class BuildModule {
    private final Path path;
    private ModuleState state;

    public BuildModule(Path path) {
        this.path = path;
    }

    public void preParse() throws CompilerError {
        if (state == null) {
            state = PreParsedModuleState.build(this);
        }
    }

    public void parse() throws CompilerError {
        if (!(state instanceof PreParsedModuleState)) {
            preParse();
        }

        if (state instanceof PreParsedModuleState preParsedModuleState) {
            state = ParsedModuleState.build(this, preParsedModuleState);
        }
    }

    public void bindSymbols() throws CompilerError {
        if (!(state instanceof ParsedModuleState)) {
            parse();
        }

        if (state instanceof ParsedModuleState parsedModuleState) {
            state = ScopeBoundModuleState.build(this, parsedModuleState);
        }
    }

    public void generate() throws CompilerError {
        if (!(state instanceof ScopeBoundModuleState)) {
            bindSymbols();
        }

        if (state instanceof ScopeBoundModuleState scopeBoundModuleState) {
            state = GeneratedModuleState.build(this, scopeBoundModuleState);
        }
    }

    public Path getPath() {
        return path;
    }

    public String toString() {
        return String.format("<BuildModule at %s : %s>", path, state);
    }
}
