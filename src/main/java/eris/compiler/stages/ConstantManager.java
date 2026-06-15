package eris.compiler.stages;

import eris.module.constant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantManager {
    private final List<Constant> constants = new ArrayList<>();

    private final Map<Integer, IntegerConstant> integerConstants = new HashMap<>();

    public IntegerConstant getIntegerConstant(int value) {
        IntegerConstant constant = integerConstants.get(value);
        if(constant == null) {
            constant = new IntegerConstant(value);
            insert(constant, value, integerConstants);
        }
        return constant;
    }

    private <T extends Constant, U> void insert(T constant, U key, Map<U, T> map) {
        map.put(key, constant);
        constants.add(constant);
    }
}
