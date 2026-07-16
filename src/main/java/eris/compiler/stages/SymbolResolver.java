package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.*;
import eris.compiler.symbol.ScopeHandler;
import eris.compiler.symbol.SymbolBuilder;
import eris.compiler.type.TypeBuilder;

import java.util.List;

public class SymbolResolver {
    private final BuildModule module;

    private final ScopeHandler scopeHandler = new ScopeHandler();
    private final TypeBuilder typeBuilder = new TypeBuilder();

    public SymbolResolver(BuildModule module) {
        this.module = module;
    }

    public void resolveSymbols() throws CompilerError {

    }
}
