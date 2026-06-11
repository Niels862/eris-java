package eris.compiler;

import eris.compiler.ast.AbstractNode;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BuildModule {
    private final Path path;
    private AbstractNode root;

    public BuildModule(Path path) {
        this.path = path;
    }

    public void parse(BuildManager manager) throws CompilerError {
        List<Token> tokens = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path)) {
            eris.compiler.Lexer lexer = new eris.compiler.Lexer(reader);

            Token token;
            do {
                token = lexer.nextToken();
                tokens.add(token);
            } while (token.kind != TokenKind.EOF);
        } catch (IOException e) {
            throw new CompilerError(String.format("Could not read %s", path));
        }

        Parser parser = new Parser(this, tokens);
        AbstractNode root = parser.parse();

        root.accept(new NodeWriter());
    }

    public Path getPath() {
        return path;
    }

    public String toString() {
        return String.format("<BuildModule at %s>", path);
    }
}
