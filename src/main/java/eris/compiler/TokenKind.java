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
    COLON(":"),
    EQ("="),
    EQEQ("=="),
    NEQ("!="),
    EQEQEQ("==="),
    NEQEQ("!=="),

    FUNC("func"),
    RETURN("return"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    DO("do"),
    LOOP("loop"),
    FOR("for"),
    VAR("var"),
    TRUE("true"),
    FALSE("false"),

    INTEGER("integer", true),
    INVALID_INTEGER("integer", true, TokenKind.INTEGER),

    STRING("string", true),
    INVALID_STRING("string", true, TokenKind.STRING),

    IDENTIFIER("identifier", true),
    EOF("end of file"),

    UNRECOGNIZED("unrecognized token", true);

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
