package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.symbol.FunctionSymbol;
import eris.module.constant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantManager {
    private final List<Constant> constants = new ArrayList<>();

    private final Map<BuildModule, ModuleReferenceConstant> moduleReferenceConstants = new HashMap<>();
    private final Map<FunctionSymbol, FunctionReferenceConstant> functionReferenceConstants = new HashMap<>();
    private final Map<Integer, IntegerConstant> integerConstants = new HashMap<>();
    private final Map<String, StringConstant> stringConstants = new HashMap<>();

    private final Map<Constant, Integer> invertedIndexMap = new HashMap<>();

    public ModuleReferenceConstant getModuleReferenceConstant(BuildModule module) {
        ModuleReferenceConstant constant = moduleReferenceConstants.get(module);
        if (constant == null) {
            StringConstant nameConstant = getStringConstant(module.name);
            constant = new ModuleReferenceConstant(nameConstant);
            insert(constant, module, moduleReferenceConstants);
        }
        return constant;
    }

    public FunctionReferenceConstant getFunctionReferenceConstant(FunctionSymbol function) {
        FunctionReferenceConstant constant = functionReferenceConstants.get(function);
        if (constant == null) {
            ModuleReferenceConstant moduleReference = getModuleReferenceConstant(function.module);
            StringConstant nameConstant = getStringConstant(function.name);
            constant = new FunctionReferenceConstant(moduleReference, nameConstant);
            insert(constant, function, functionReferenceConstants);
        }
        return constant;
    }

    public IntegerConstant getIntegerConstant(int value) {
        IntegerConstant constant = integerConstants.get(value);
        if (constant == null) {
            constant = new IntegerConstant(value);
            insert(constant, value, integerConstants);
        }
        return constant;
    }

    public StringConstant getStringConstant(String value) {
        StringConstant constant = stringConstants.get(value);
        if (constant == null) {
            constant = new StringConstant(value);
            insert(constant, value, stringConstants);
        }
        return constant;
    }

    public int getIndexOf(Constant constant) {
        assert constants.indexOf(constant) == invertedIndexMap.get(constant);
        return invertedIndexMap.get(constant);
    }

    public List<Constant> getConstants() {
        return constants;
    }

    private <T extends Constant, U> void insert(T constant, U key, Map<U, T> map) {
        map.put(key, constant);
        int index = constants.size();
        constants.add(constant);
        invertedIndexMap.put(constant, index);
    }
}
