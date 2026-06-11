package eris.compiler;

import eris.compiler.ast.ASTNode;
import eris.compiler.Lexer;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BuildModule {
    private final Path path;
    private ASTNode root;

    public BuildModule(Path path) {
        this.path = path;
    }

    public void parse(BuildManager manager) throws CompilerError {
        List<Token> tokens = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path)) {
            Lexer lexer = new Lexer(reader);

            Token token;
            do {
                token = lexer.nextToken();
                tokens.add(token);
            } while (token.kind != TokenKind.EOF);
        } catch (IOException e) {
            throw new CompilerError(String.format("Could not read %s", path));
        }

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
