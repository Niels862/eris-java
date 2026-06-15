package eris.module;

import eris.module.constant.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Module {
    public final String name;
    public final List<Function> functions;
    public final List<Constant> constants;

    public Module(String name, List<Function> functions, List<Constant> constants) {
        this.name = name;
        this.functions = functions;
        this.constants = constants;
    }

    public Function lookupFunction(String name) {
        for (Function function : functions) {
            if (function.name.equals(name)) {
                return function;
            }
        }
        return null;
    }

    public void dump() {
        System.out.println("Module " + this.name);
        System.out.println("Constants:");
        for (int i = 0; i < this.constants.size(); i++) {
            System.out.println("  [" + i + "]: " + this.constants.get(i));
        }
        System.out.println("Functions:");
        for (int i = 0; i < this.functions.size(); i++) {
            System.out.println("  [" + i + "]: " + this.functions.get(i));
        }
        System.out.println();
    }
}
