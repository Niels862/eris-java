package eris;

import eris.compiler.Compiler;

public class Main {
    static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: eris <input_file>");
            System.exit(1);
        }

        String name = args[0];

        Compiler compiler = new Compiler(name);
        compiler.compile();
    }
}
