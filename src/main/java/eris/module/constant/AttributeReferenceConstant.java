package eris.module.constant;

public class AttributeReferenceConstant extends Constant {
    public final ClassReferenceConstant clazz;
    public final StringConstant name;

    public AttributeReferenceConstant(ClassReferenceConstant clazz, StringConstant name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Attribute %s::%s::%s", clazz.module.name.value, clazz.name.value, name.value);
    }
}
