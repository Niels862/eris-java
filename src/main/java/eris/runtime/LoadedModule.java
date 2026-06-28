package eris.runtime;

import eris.module.Module;
import eris.module.Function;
import eris.module.constant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadedModule {
    public final String name;
    public final List<Constant> constants;
    public final Object[] resolvedConstants;

    private final Module module;
    private final ModuleManager manager;

    private final Map<ModuleReferenceConstant, LoadedModule> resolvedModules = new HashMap<>();
    private final Map<FunctionReferenceConstant, ResolvedFunctionReference> resolvedFunctions = new HashMap<>();

    public LoadedModule(Module module, ModuleManager manager) {
        this.name = module.name;
        this.constants = module.constants;
        this.resolvedConstants = new Object[constants.size()];
        this.module = module;
        this.manager = manager;

        resolveConstantValues();
    }

    private void resolveConstantValues() {
        for (int i = 0; i < constants.size(); i++) {
            Constant constant = constants.get(i);
            resolvedConstants[i] = switch (constant) {
                case IntegerConstant integerConstant
                        -> integerConstant.value;

                case StringConstant stringConstant
                        -> stringConstant.value;

                default
                        -> null;
            };
        }
    }

    public Function getEntryFunction() {
        return module.lookupFunction("$entry");
    }

    public LoadedModule resolveModule(ModuleReferenceConstant reference) {
        LoadedModule resolved = resolvedModules.get(reference);
        if (resolved != null) {
            return resolved;
        }

        resolved = manager.getModuleByName(reference.name.value);
        resolvedModules.put(reference, resolved);
        return resolved;
    }

    public ResolvedFunctionReference resolveFunction(FunctionReferenceConstant reference) {
        ResolvedFunctionReference resolved = resolvedFunctions.get(reference);
        if (resolved != null) {
            return resolved;
        }

        LoadedModule resolvedModule = resolveModule(reference.module);
        Function resolvedFunction = resolvedModule.module.lookupFunction(reference.name.value);
        resolved = new ResolvedFunctionReference(resolvedModule, resolvedFunction);
        resolvedFunctions.put(reference, resolved);
        return resolved;
    }

    public static class ResolvedFunctionReference {
        public final LoadedModule module;
        public final Function function;

        public ResolvedFunctionReference(LoadedModule module, Function function) {
            this.module = module;
            this.function = function;
        }
    }
}
