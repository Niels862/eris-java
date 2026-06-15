package eris.compiler.modulestate;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.stages.ConstantManager;
import eris.compiler.stages.IntermediateCodeGenerator;

import java.util.List;

public class GeneratedModuleState extends ModuleState {
    private final List<BuildFunction> functions;
    private final ConstantManager constants;

    public GeneratedModuleState(List<BuildFunction> functions, ConstantManager constants) {
        this.functions = functions;
        this.constants = constants;
    }

    public static GeneratedModuleState build(BuildModule module, ScopeBoundModuleState state) throws CompilerError {
        ConstantManager constants = new ConstantManager();

        IntermediateCodeGenerator generator = new IntermediateCodeGenerator(module, state.getModuleNode(), constants);
        List<BuildFunction> functions = generator.generate();

        for (BuildFunction function : functions) {
            function.dump();
            System.out.println();
        }

        return new GeneratedModuleState(functions, constants);
    }

    public List<BuildFunction> getFunctions() {
        return functions;
    }

    public ConstantManager getConstantManager() {
        return constants;
    }
}
