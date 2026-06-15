package eris.runtime;

import eris.module.Function;
import eris.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Interpreter {
    private final Module module;
    private final List<Object> stack = new ArrayList<>();

    public Interpreter(Module module) {
        this.module = module;
    }

    public void run() {
        Function entry = module.lookupFunction("$entry");
        if (entry == null) {
            throw new RuntimeException("Entry function not found");
        }

        Object output = callFunction(module, entry);
        System.out.println("Finished with output: " + output);
    }

    public Object callFunction(Module module, Function function) {
        return null;
    }
}
