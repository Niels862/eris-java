package eris.compiler.modulestate;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.stages.BuildFunctionGenerator;
import eris.compiler.stages.ConstantManager;

import java.util.List;

public class GeneratedModuleState extends ModuleState {
    private final List<BuildFunction> functions;

    public GeneratedModuleState(List<BuildFunction> functions) {
        this.functions = functions;
    }

    public static GeneratedModuleState build(BuildModule module, ScopeBoundModuleState state) throws CompilerError {
        BuildFunctionGenerator generator = new BuildFunctionGenerator(module);
        List<BuildFunction> functions = generator.generate(state.getModuleNode());

        for (BuildFunction function : functions) {
            function.dump();
            System.out.println();
        }

        return new GeneratedModuleState(functions);
    }

    public List<BuildFunction> getFunctions() {
        return functions;
    }
}
