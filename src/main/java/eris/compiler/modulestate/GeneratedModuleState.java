package eris.compiler.modulestate;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.FunctionNode;
import eris.compiler.ast.ModuleNode;
import eris.compiler.stages.IntermediateCodeGenerator;

import java.util.List;

public class GeneratedModuleState extends ModuleState {
    private final List<BuildFunction> functions;

    public GeneratedModuleState(List<BuildFunction> functions) {
        this.functions = functions;
    }

    public static GeneratedModuleState build(BuildModule module, ScopeBoundModuleState state) throws CompilerError {
        IntermediateCodeGenerator generator = new IntermediateCodeGenerator(module, state.getModuleNode());
        List<BuildFunction> functions = generator.generate();

        for (BuildFunction function : functions) {
            System.out.println(function);
        }

        return new GeneratedModuleState(functions);
    }

    public List<BuildFunction> getFunctions() {
        return functions;
    }
}
