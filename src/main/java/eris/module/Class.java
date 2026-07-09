package eris.module;

public class Class {
    public final String name;

    public Class(String name) {
        this.name = name;
    }

    public void dump() {
        System.out.println("Class " + name);
    }

    public String toString() {
        return "Class " + name;
    }
}
