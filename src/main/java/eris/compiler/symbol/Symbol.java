package eris.compiler.symbol;

public abstract class Symbol {
    private final String name;
    private final int line;
    private final int column;

    public Symbol(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }
}
