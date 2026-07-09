package eris.module;

import eris.module.constant.Constant;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public final String name;
    public final List<Class> classes;
    public final List<Function> functions;
    public final List<Constant> constants;
    public final int entryFunctionReference;

    public Module(
            String name,
            List<Class> classes,
            List<Function> functions,
            List<Constant> constants,
            int entryFunctionReference) {
        this.name = name;
        this.classes = classes;
        this.functions = functions;
        this.constants = constants;
        this.entryFunctionReference = entryFunctionReference;
    }

    public Class lookupClass(String name) {
        for (Class clazz : classes) {
            if (clazz.name.equals(name)) {
                return clazz;
            }
        }
        return null;
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
        System.out.println("Module " + this.name + " [" + entryFunctionReference + "]");

        System.out.println("Constants:");
        for (int i = 0; i < this.constants.size(); i++) {
            System.out.println("[" + i + "]: " + this.constants.get(i));
        }

        System.out.println("Classes:");
        for (int i = 0; i < this.classes.size(); i++) {
            System.out.print("[" + i + "]: ");
            this.classes.get(i).dump();
        }

        System.out.println("Functions:");
        for (int i = 0; i < this.functions.size(); i++) {
            System.out.print("[" + i + "]: ");
            this.functions.get(i).dump();
        }
        System.out.println();
    }
}
