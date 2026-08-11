package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.Interner;
import eris.compiler.symbol.ClassSymbol;
import eris.compiler.symbol.FunctionSymbol;
import eris.module.constant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantManager {
    private final List<Constant> constants = new ArrayList<>();

    public final Interner<Integer, IntegerConstant> integers = new Interner<>(IntegerConstant::new, this::add);

    public final Interner<String, StringConstant> strings = new Interner<>(StringConstant::new, this::add);

    public final Interner<BuildModule, ModuleReferenceConstant> modules = new Interner<>(key -> {
        StringConstant name = strings.get(key.name);
        return new ModuleReferenceConstant(name);
    }, this::add);

    public final Interner<ClassSymbol, ClassReferenceConstant> classes = new Interner<>(key -> {
        ModuleReferenceConstant module = modules.get(key.getModule());
        StringConstant name = strings.get(key.name);
        return new ClassReferenceConstant(module, name);
    }, this::add);

    public final Interner<FunctionSymbol, FunctionReferenceConstant> functions = new Interner<>(key -> {
        ModuleReferenceConstant module = modules.get(key.getModule());
        StringConstant name = strings.get(key.name);
        return new FunctionReferenceConstant(module, name);
    }, this::add);

    private final Map<BuildModule, ModuleReferenceConstant> moduleReferenceConstants = new HashMap<>();
    private final Map<ClassSymbol, ClassReferenceConstant> classReferenceConstants = new HashMap<>();
    private final Map<FunctionSymbol, FunctionReferenceConstant> functionReferenceConstants = new HashMap<>();
    private final Map<Integer, IntegerConstant> integerConstants = new HashMap<>();
    private final Map<String, StringConstant> stringConstants = new HashMap<>();

    private final Map<Constant, Integer> invertedIndexMap = new HashMap<>();

    public int getIndexOf(Constant constant) {
        assert constants.indexOf(constant) == invertedIndexMap.get(constant);
        return invertedIndexMap.get(constant);
    }

    public List<Constant> getConstants() {
        return constants;
    }

    private <V extends Constant> void add(V constant) {
        int index = constants.size();
        constants.add(constant);
        invertedIndexMap.put(constant, index);
    }
}
