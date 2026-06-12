package eris.compiler.modulestate;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.TokenKind;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PreParsedModuleState extends ModuleState {
    private final List<Token> tokens;

    public PreParsedModuleState(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static PreParsedModuleState build(BuildModule module) throws CompilerError {
        List<Token> tokens = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(module.getPath())) {
            eris.compiler.Lexer lexer = new eris.compiler.Lexer(reader);

            Token token;
            do {
                token = lexer.nextToken();
                tokens.add(token);
            } while (token.kind != TokenKind.EOF);
        } catch (IOException e) {
            throw new CompilerError(String.format("Could not read %s", module.getPath()));
        }

        return new PreParsedModuleState(tokens);
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
