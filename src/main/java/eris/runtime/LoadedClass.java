package eris.runtime;

import eris.module.Attribute;
import eris.module.Class;
import eris.module.constant.AttributeReferenceConstant;

import java.util.HashMap;
import java.util.Map;

public class LoadedClass {
    public final LoadedModule module;
    public final Class clazz;

    public final Map<String, LoadedAttribute> attributes = new HashMap<>();
    public final int size;

    public LoadedClass(LoadedModule module, Class clazz) {
        this.module = module;
        this.clazz = clazz;

        int offset = 0;
        for (Attribute attribute : clazz.attributes) {
            attributes.put(attribute.name, new LoadedAttribute(attribute, offset));
            offset++;
        }

        this.size = offset;
    }

    public LoadedAttribute resolveAttribute(AttributeReferenceConstant reference) {
        return attributes.get(reference.name.value);
    }
}
