package eris.compiler;

public enum TokenKind {
    LBRACE("{"),
    RBRACE("}"),
    LBRACKET("["),
    RBRACKET("]"),
    LPAREN("("),
    RPAREN(")"),
    COMMA(","),
    SEMICOLON(";"),

    RETURN("return"),

    NUMBER("number"),
    STRING("string"),
    IDENTIFIER("", "identifier"),
    EOF("", "end of file"),

    UNRECOGNIZED("", "unrecognized");

    public final String sourceText;
    public final String userText;

    TokenKind(String sourceText) {
        this.sourceText = sourceText;
        this.userText = sourceText;
    }

    TokenKind(String sourceText, String userText) {
        this.sourceText = sourceText;
        this.userText = userText;
    }
}
