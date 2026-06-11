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

    NUMBER,
    STRING,
    IDENTIFIER,
    EOF,

    UNRECOGNIZED
}
