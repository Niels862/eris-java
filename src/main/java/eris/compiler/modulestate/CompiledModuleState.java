package eris.compiler.modulestate;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.stages.FunctionGenerator;
import eris.compiler.stages.ConstantManager;
import eris.compiler.symbol.ClassSymbol;
import eris.compiler.symbol.Symbol;
import eris.module.Class;
import eris.module.Function;
import eris.module.Module;
import eris.module.constant.Constant;
import eris.module.constant.FunctionReferenceConstant;

import java.util.ArrayList;
import java.util.List;

public class CompiledModuleState extends ModuleState {
    private final Module compiledModule;

    public CompiledModuleState(Module compiledModule) {
        this.compiledModule = compiledModule;
    }

    public static CompiledModuleState build(BuildModule module, GeneratedModuleState state) {
        ConstantManager constantManager = new ConstantManager();

        List<Class> classes = new ArrayList<>();
        List<Function> functions = new ArrayList<>();
        List<Constant> constants = constantManager.getConstants();

        for (Symbol symbol : state.definitions.getSymbols().values()) {
            if (symbol instanceof ClassSymbol) {
                classes.add(new Class(symbol.name));
            }
        }

        for (BuildFunction function : state.functions) {
            FunctionGenerator compiler = new FunctionGenerator(function, constantManager);
            functions.add(compiler.compile());
        }

        FunctionReferenceConstant entryReference = constantManager.getFunctionReferenceConstant(state.entrySymbol);
        int entryIndex = constantManager.getIndexOf(entryReference);

        Module compiledModule = new Module(module.name, classes, functions, constants, entryIndex);
        compiledModule.dump();

        return new CompiledModuleState(compiledModule);
    }

    public Module getCompiledModule() {
        return compiledModule;
    }
}
