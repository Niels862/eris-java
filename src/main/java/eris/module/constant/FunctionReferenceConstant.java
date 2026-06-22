package eris.module.constant;

public class FunctionReferenceConstant extends Constant {
    public final ModuleReferenceConstant module;
    public final StringConstant name;

    public FunctionReferenceConstant(ModuleReferenceConstant module, StringConstant name) {
        this.module = module;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Function " + module.name + "::" + name;
    }
}
