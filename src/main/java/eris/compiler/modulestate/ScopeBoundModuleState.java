package eris.compiler.modulestate;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.ModuleNode;
import eris.compiler.stages.SymbolBinder;

public class ScopeBoundModuleState extends ModuleState {
    private final ModuleNode moduleNode;

    public ScopeBoundModuleState(ModuleNode moduleNode) {
        this.moduleNode = moduleNode;
    }

    public static ScopeBoundModuleState build(BuildModule module, ParsedModuleState state) throws CompilerError {
        ModuleNode moduleNode = state.getModuleNode();
        SymbolBinder symbolBinder = new SymbolBinder(module, moduleNode);
        symbolBinder.bindSymbols();
        return new ScopeBoundModuleState(moduleNode);
    }

    public ModuleNode getModuleNode() {
        return moduleNode;
    }
}
