package eris.compiler;

public class CompilerError extends RuntimeException {
    public CompilerError(String message) {
        super(message);
    }
}
