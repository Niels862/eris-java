package eris.compiler;

public enum TokenKind {
    ARROW("->"),
    LPAREN("("),
    RPAREN(")"),
    LBRACKET("["),
    RBRACKET("]"),
    LBRACE("{"),
    RBRACE("}"),
    COMMA(","),
    SEMICOLON(";"),

    FUNC("func"),
    RETURN("return"),

    INTEGER("integer", true),
    INVALID_INTEGER("integer", true, TokenKind.INTEGER),

    STRING("string", true),
    IDENTIFIER("identifier", true),
    EOF("end of file"),

    UNRECOGNIZED("unrecognized token");

    public final String userString;
    public final boolean hasData;
    public final TokenKind invalidVariantOf;

    TokenKind(String userString) {
        this.userString = userString;
        this.hasData = false;
        this.invalidVariantOf = null;
    }

    TokenKind(String userString, boolean hasData) {
        this.userString = userString;
        this.hasData = hasData;
        this.invalidVariantOf = null;
    }

    TokenKind(String userString, boolean hasData, TokenKind invalidVariantOf) {
        this.userString = userString;
        this.hasData = hasData;
        this.invalidVariantOf = invalidVariantOf;
    }
}
