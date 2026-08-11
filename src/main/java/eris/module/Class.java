package eris.module;

import java.util.List;

public class Class {
    public final String name;
    public final List<Attribute> attributes;

    public Class(String name, List<Attribute> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public void dump() {
        System.out.println("Class " + name);
        for (Attribute attribute : attributes) {
            System.out.println("  " + attribute);
        }
    }

    public String toString() {
        return "Class " + name;
    }
}
