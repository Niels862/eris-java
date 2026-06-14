package eris.compiler.symbol;

public abstract class Symbol {
    public final String name;
    public final int line;
    public final int column;

    public Symbol(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public abstract String toString();
}
