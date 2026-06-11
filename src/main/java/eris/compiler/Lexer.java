package eris.compiler;

import java.io.IOException;
import java.util.*;

public class Lexer {
    private final String text;
    private final List<Token> tokens = new ArrayList<>();

    private LexerState state = LexerState.initial();
    private LexerState base = LexerState.initial();

    private final TokenKind[] specialTokenKinds = {
        TokenKind.LBRACE,
        TokenKind.RBRACE,
        TokenKind.LBRACKET,
        TokenKind.RBRACKET,
        TokenKind.LPAREN,
        TokenKind.RPAREN,
        TokenKind.COMMA,
        TokenKind.SEMICOLON,
    };

    private final TokenKind[] keywordTokenKinds = {
        TokenKind.RETURN,
    };

    private final Set<Integer> specialCodePoints = getCodePoints(specialTokenKinds);

    public Lexer(String text) {
        this.text = text;
    }

    public List<Token> lex() throws CompilerError {
        while (state.index < text.length()) {
            nextToken();
        }

        emit(TokenKind.EOF);
        return tokens;
    }

    private void nextToken() throws CompilerError {
        int codepoint = startNextToken();

        if (Character.isLetter(codepoint) || codepoint == '_') {
            nextIdentifier();
        } else if (Character.isDigit(codepoint)) {
            nextNumber();
        } else if (specialCodePoints.contains(codepoint)) {
            nextSpecialToken();
        } else if (Character.isWhitespace(codepoint)) {
            skipWhitespace();
        } else {
            throw new CompilerError("Unrecognized character: " + Character.toString(codepoint));
        }
    }

    private void nextIdentifier() {
        do {
            state.advance(text);
        } while (Character.isLetterOrDigit(state.codepoint) || state.codepoint == '_');

        String tokenText = getCurrentTokenText();
        for (TokenKind kind : keywordTokenKinds) {
            if (kind.sourceText.equals(tokenText)) {
                emit(kind);
                return;
            }
        }

        emit(TokenKind.IDENTIFIER);
    }

    private void nextNumber() {
        do {
            state.advance(text);
        } while (Character.isDigit(state.codepoint));

        emit(TokenKind.NUMBER);
    }

    private void nextSpecialToken() throws CompilerError {
        LexerState matchEnd = null;
        TokenKind matchKind = null;

        do {
            state.advance(text);

            String tokenText = getCurrentTokenText();
            for (TokenKind kind : specialTokenKinds) {
                if (kind.sourceText.equals(tokenText)) {
                    matchEnd = state.copy();
                    matchKind = kind;
                }
            }
        } while (specialCodePoints.contains(state.codepoint));

        if (matchKind == null) {
            throw new CompilerError(String.format("Invalid token: %s", getCurrentTokenText()));
        }

        state = matchEnd;
        emit(matchKind);
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(state.codepoint)) {
            state.advance(text);
        }
    }

    private int startNextToken() {
        base = state.copy();
        return state.readCodePoint(text);
    }

    private String getCurrentTokenText() {
        return text.substring(base.index, state.index);
    }

    private void emit(TokenKind kind) {
        emit(new Token(kind, text.substring(base.index, state.index), base.line, base.column));
    }

    private void emit(Token token) {
        tokens.add(token);
    }

    private Set<Integer> getCodePoints(TokenKind[] tokenKinds) {
        Set<Integer> codePoints = new HashSet<>();

        for (TokenKind kind : tokenKinds) {
            getCodePoints(kind.sourceText, codePoints);
        }

        return codePoints;
    }

    private void getCodePoints(String text, Set<Integer> codePoints) {
        int index = 0;

        while (index < text.length()) {
            int codePoint = text.codePointAt(index);
            codePoints.add(codePoint);
            index += Character.charCount(codePoint);
        }
    }

    private static class LexerState {
        public int line;
        public int column;
        public int index;
        public int codepoint;

        private LexerState(int line, int column, int index, int codepoint) {
            this.line = line;
            this.column = column;
            this.index = index;
            this.codepoint = codepoint;
        }

        public static LexerState initial() {
            return new LexerState(1, 1, 0, 0);
        }

        public LexerState copy() {
            return new LexerState(line, column, index, codepoint);
        }

        public int readCodePoint(String text) {
            if (index >= text.length()) {
                codepoint = 0;
            } else {
                codepoint = text.codePointAt(index);
            }
            return codepoint;
        }

        public void advance(String text) {
            assert index < text.length();
            assert codepoint > 0;

            if (codepoint == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }

            index += Character.charCount(codepoint);
            readCodePoint(text);
        }
    }
}
