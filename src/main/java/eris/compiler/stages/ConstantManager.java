package eris.compiler.stages;

import eris.module.constant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantManager {
    private final List<Constant> constants = new ArrayList<>();

    private final Map<String, ModuleReferenceConstant> moduleReferenceConstants = new HashMap<>();
    private final Map<String, FunctionReferenceConstant> functionReferenceConstants = new HashMap<>();
    private final Map<Integer, IntegerConstant> integerConstants = new HashMap<>();
    private final Map<String, StringConstant> stringConstants = new HashMap<>();

    private final Map<Constant, Integer> invertedIndexMap = new HashMap<>();

    public ModuleReferenceConstant getModuleReferenceConstant(String name) {
        ModuleReferenceConstant constant = moduleReferenceConstants.get(name);
        if (constant == null) {
            StringConstant nameConstant = getStringConstant(name);
            constant = new ModuleReferenceConstant(nameConstant);
            insert(constant, name, moduleReferenceConstants);
        }
        return constant;
    }

    public FunctionReferenceConstant getFunctionReferenceConstant(String moduleName, String functionName) {
        String key = moduleName + "::" + functionName;
        FunctionReferenceConstant constant = functionReferenceConstants.get(key);
        if (constant == null) {
            ModuleReferenceConstant moduleReference = getModuleReferenceConstant(moduleName);
            StringConstant nameConstant = getStringConstant(functionName);
            constant = new FunctionReferenceConstant(moduleReference, nameConstant);
            insert(constant, key, functionReferenceConstants);
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
