package eris.compiler;

public enum TokenKind {
    LPAREN,
    RPAREN,
    LBRACKET,
    RBRACKET,
    LBRACE,
    RBRACE,
    COMMA,
    SEMICOLON,

    RETURN,

    INTEGER,
    STRING,
    IDENTIFIER,
    EOF,

    UNRECOGNIZED
}
