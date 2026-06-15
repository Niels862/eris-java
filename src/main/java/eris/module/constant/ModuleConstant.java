package eris.module.constant;

public class ModuleConstant extends Constant {
    public final StringConstant name;

    public ModuleConstant(StringConstant name) {
        this.name = name;
    }

    public String toString() {
        return "Module " + name.value;
    }
}
