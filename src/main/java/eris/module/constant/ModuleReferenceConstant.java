package eris.module.constant;

public class ModuleReferenceConstant extends Constant {
    public final StringConstant name;

    public ModuleReferenceConstant(StringConstant name) {
        this.name = name;
    }

    public String toString() {
        return "Module " + name.value;
    }
}
