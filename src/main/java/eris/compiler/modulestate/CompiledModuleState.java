package eris.compiler.modulestate;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.stages.FunctionCompiler;
import eris.compiler.stages.ConstantManager;
import eris.module.Function;
import eris.module.Module;
import eris.module.constant.Constant;

import java.util.ArrayList;
import java.util.List;

public class CompiledModuleState extends ModuleState {
    private final Module compiledModule;

    public CompiledModuleState(Module compiledModule) {
        this.compiledModule = compiledModule;
    }

    public static CompiledModuleState build(BuildModule module, GeneratedModuleState generatedModuleState) {
        ConstantManager constantManager = generatedModuleState.getConstantManager();

        List<Function> functions = new ArrayList<>();
        for (BuildFunction function : generatedModuleState.getFunctions()) {
            FunctionCompiler compiler = new FunctionCompiler(function, constantManager);
            functions.add(compiler.compile());
        }

        List<Constant> constants = constantManager.getConstants();
        Module compiledModule = new Module(module.name, functions, constants);

        compiledModule.dump();

        return new CompiledModuleState(compiledModule);
    }

    public Module getCompiledModule() {
        return compiledModule;
    }
}
