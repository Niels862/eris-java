package eris.module.constant;

public class AttributeReferenceConstant extends Constant {
    public final ClassReferenceConstant clazz;

    public AttributeReferenceConstant(ClassReferenceConstant clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return String.format("Attribute %s.*", clazz);
    }
}
