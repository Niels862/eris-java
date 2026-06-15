package eris.compiler;

import eris.compiler.modulestate.*;
import eris.module.Module;

import java.nio.file.Path;

public class BuildModule {
    private final String name;
    private final Path path;
    private ModuleState state;

    public BuildModule(String name, Path path) {
        this.name = name;
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

    public Module compile() throws CompilerError {
        if (!(state instanceof GeneratedModuleState)) {
            generate();
        }

        if (state instanceof GeneratedModuleState generatedModuleState) {
            state = CompiledModuleState.build(this, generatedModuleState);
        }

        if (state instanceof CompiledModuleState compiledModuleState) {
            return compiledModuleState.getCompiledModule();
        } else {
            throw new RuntimeException("Unexpected state: " + state);
        }
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public String toString() {
        return String.format("<BuildModule at %s : %s>", path, state);
    }
}
