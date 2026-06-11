package eris.compiler;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String text;
    private final List<Token> tokens = new ArrayList<>();

    private int line = 1;
    private int column = 1;

    public Lexer(String text) {
        this.text = text;
    }

    public List<Token> lex() {
        emit(TokenKind.EOF);
        return tokens;
    }

    private void emit(TokenKind kind) {
        emit(new Token(kind, "", line, column));
    }

    private void emit(TokenKind kind, String text) {
        tokens.add(new Token(kind, text, line, column));
    }

    private void emit(Token token) {
        tokens.add(token);
    }
}
