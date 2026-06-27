package eris;

import eris.compiler.Compiler;
import eris.module.Module;
import eris.runtime.Interpreter;

import java.util.List;

public class Main {
    static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: eris <input_file>");
            System.exit(1);
        }

        String name = args[0];

        Compiler compiler = new Compiler(name);
        List<Module> modules = compiler.compile();

        if (modules != null) {
            Interpreter interpreter = new Interpreter(modules.getFirst());
            interpreter.run();
        }
    }
}
