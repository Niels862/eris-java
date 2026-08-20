package eris.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuiltinLibrary {
    private static final BuiltinLibrary instance = new BuiltinLibrary();

    private final Map<String, Implementation> implementations = new HashMap<>();

    private BuiltinLibrary() {
        register("test", new SystemImplementation());
    }

    private void register(String name, Implementation implementation) {
        implementations.put(name, implementation);
    }

    public static BuiltinLibrary.Implementation lookup(String name) {
        return instance.implementations.get(name);
    }

    public abstract static class Implementation {
        private final Map<String, NativeFunction> functions = new HashMap<>();

        public Implementation() {
            load();
        }

        public NativeFunction lookup(String name) {
            return functions.get(name);
        }

        protected void register(String name, NativeFunction function) {
            functions.put(name, function);
        }

        abstract void load();
    }

    public static class SystemImplementation extends Implementation {
        @Override
        void load() {
            register("print", this::print);
        }

        public Object print(List<Object> args) {
            System.out.println(args.getFirst());
            return null;
        }
    }
}
