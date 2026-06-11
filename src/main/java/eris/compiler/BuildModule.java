package eris.compiler;

import eris.compiler.ast.ASTNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BuildModule {
    private final Path path;
    private ASTNode root;

    public BuildModule(Path path) {
        this.path = path;
    }

    public void parse(BuildManager manager) throws CompilerError {
        String text;
        try {
            text = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new CompilerError(String.format("Could not read %s", path));
        }

        Lexer lexer = new Lexer(text);
        List<Token> tokens = lexer.lex();

        System.out.println("{");
        for (Token token : tokens) {
            System.out.println("  " + token);
        }
        System.out.println("}");
    }

    public String toString() {
        return String.format("<BuildModule at %s>", path);
    }
}
