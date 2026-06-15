package eris.module.constant;

public class FunctionConstant extends Constant {
    public final ModuleConstant module;
    public final StringConstant name;

    public FunctionConstant(ModuleConstant module, StringConstant name) {
        this.module = module;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Function " + module.name + "::" + name;
    }
}
