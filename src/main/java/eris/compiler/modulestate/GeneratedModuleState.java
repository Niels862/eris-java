package eris.compiler.modulestate;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.stages.BuildFunctionGenerator;
import eris.compiler.stages.SemanticAnalyzer;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.SymbolTable;

import java.util.List;

public class GeneratedModuleState extends ModuleState {
    public final List<BuildFunction> functions;
    public final SymbolTable definitions;
    public final FunctionSymbol entrySymbol;

    public GeneratedModuleState(List<BuildFunction> functions, SymbolTable definitions, FunctionSymbol entrySymbol) {
        this.functions = functions;
        this.definitions = definitions;
        this.entrySymbol = entrySymbol;
    }

    public static GeneratedModuleState build(BuildModule module, ScopeBoundModuleState state) throws CompilerError {
        BuildFunctionGenerator generator = new BuildFunctionGenerator(module);
        List<BuildFunction> functions = generator.generate(state.moduleNode);

        for (BuildFunction function : functions) {
            SemanticAnalyzer analyzer = new SemanticAnalyzer(module, function);
            analyzer.analyze();
            function.dump();
        }

        return new GeneratedModuleState(functions, state.definitions, state.moduleNode.entrySymbol);
    }

    public List<BuildFunction> getFunctions() {
        return functions;
    }
}
