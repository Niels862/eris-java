package eris.runtime;

import eris.module.Class;
import eris.module.Module;
import eris.module.Function;
import eris.module.constant.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadedModule {
    public final String name;
    public final List<Constant> constants;
    public final Object[] resolvedConstants;
    public final LoadedFunction entryFunction;

    private final Module module;
    private final ModuleManager manager;

    private final Map<ModuleReferenceConstant, LoadedModule> resolvedModules = new HashMap<>();
    private final Map<ClassReferenceConstant, LoadedClass> resolvedClasses = new HashMap<>();
    private final Map<FunctionReferenceConstant, LoadedFunction> resolvedFunctions = new HashMap<>();

    public LoadedModule(Module module, ModuleManager manager) {
        this.name = module.name;
        this.constants = module.constants;
        this.resolvedConstants = new Object[constants.size()];
        this.entryFunction = new LoadedFunction(this, module.functions.get(module.entryFunctionReference));
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

    public LoadedModule resolveModule(ModuleReferenceConstant reference) {
        LoadedModule resolved = resolvedModules.get(reference);
        if (resolved != null) {
            return resolved;
        }

        resolved = manager.getModuleByName(reference.name.value);
        resolvedModules.put(reference, resolved);
        return resolved;
    }

    public LoadedClass resolveClass(ClassReferenceConstant reference) {
        LoadedClass resolved = resolvedClasses.get(reference);
        if (resolved != null) {
            return resolved;
        }

        LoadedModule resolvedModule = resolveModule(reference.module);
        if (resolvedModule == this) {
            Class clazz = resolvedModule.module.lookupClass(reference.name.value);
            resolved = new LoadedClass(this, clazz);
        } else {
            resolved = resolvedModule.resolveClass(reference);
        }

        resolvedClasses.put(reference, resolved);
        return resolved;
    }

    public LoadedFunction resolveFunction(FunctionReferenceConstant reference) {
        LoadedFunction resolved = resolvedFunctions.get(reference);
        if (resolved != null) {
            return resolved;
        }

        LoadedModule resolvedModule = resolveModule(reference.module);
        if (resolvedModule == this) {
            Function function = resolvedModule.module.lookupFunction(reference.name.value);
            resolved = new LoadedFunction(this, function);
        } else {
            resolved = resolvedModule.resolveFunction(reference);
        }

        resolvedFunctions.put(reference, resolved);
        return resolved;
    }
}
