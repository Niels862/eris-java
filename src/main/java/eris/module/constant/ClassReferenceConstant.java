package eris.module.constant;

public class ClassReferenceConstant extends Constant {
    public final ModuleReferenceConstant module;
    public final StringConstant name;

    public ClassReferenceConstant(ModuleReferenceConstant module, StringConstant name) {
        this.module = module;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Class " + module.name.value + "::" + name.value;
    }
}
