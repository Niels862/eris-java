package eris.module;

public class Attribute {
    public final String name;
    public final TypeTag type;

    public Attribute(String name, TypeTag type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("Attribute %s (%s)", name, type);
    }
}
