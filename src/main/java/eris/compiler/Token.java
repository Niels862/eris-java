package eris.compiler;

import org.apache.commons.text.StringEscapeUtils;

public class Token {
    public final TokenKind kind;
    public final String text;
    public final int line;
    public final int column;

    public Token(TokenKind kind, String text, int line, int column) {
        this.kind = kind;
        this.text = text;
        this.line = line;
        this.column = column;
    }

    public String toString() {
        if (text.isEmpty()) {
            return String.format("<Token %s %d:%d>", kind, line, column);
        }

        return String.format("<Token %s '%s' %d:%d>", kind, StringEscapeUtils.escapeJava(text), line, column);
    }
}
