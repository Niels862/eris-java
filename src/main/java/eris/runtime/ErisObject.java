package eris.runtime;

public class ErisObject {
    public final LoadedClass meta;
    public final Object[] attributes;

    public ErisObject(LoadedClass meta, Object[] attributes) {
        this.meta = meta;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return String.format("<ErisObject %s>", meta.clazz.name);
    }
}
