package eris.compiler.modulestate;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.ModuleNode;
import eris.compiler.stages.SymbolBinder;
import eris.compiler.symbol.SymbolTable;

public class ScopeBoundModuleState extends ModuleState {
    public final ModuleNode moduleNode;
    public final SymbolTable definitions;

    public ScopeBoundModuleState(ModuleNode moduleNode, SymbolTable definitions) {
        this.moduleNode = moduleNode;
        this.definitions = definitions;
    }

    public static ScopeBoundModuleState build(BuildModule module, ParsedModuleState state) throws CompilerError {
        ModuleNode moduleNode = state.moduleNode;
        SymbolBinder symbolBinder = new SymbolBinder(module, moduleNode, state.definitions);
        symbolBinder.bindSymbols();
        return new ScopeBoundModuleState(moduleNode, state.definitions);
    }

    public ModuleNode getModuleNode() {
        return moduleNode;
    }
}
