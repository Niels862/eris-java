package eris.runtime;

import eris.module.Attribute;

public class LoadedAttribute {
    public final Attribute attribute;
    public final int offset;

    public LoadedAttribute(Attribute attribute, int offset) {
        this.attribute = attribute;
        this.offset = offset;
    }
}
