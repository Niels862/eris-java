package eris.compiler;

public class CompilerError extends Exception {
    public CompilerError(BuildModule module, int line, int column, String message) {
        super(module.path + ":" + line + ":" + column + ": " + message);
    }

    public CompilerError(BuildModule module, String message) {
        super(module.path + ": " + message);
    }

    public CompilerError(String message) {
        super(message);
    }
}
