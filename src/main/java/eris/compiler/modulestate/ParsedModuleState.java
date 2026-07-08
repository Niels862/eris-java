package eris.compiler.modulestate;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.stages.Parser;
import eris.compiler.ast.ModuleNode;
import eris.compiler.ast.NodeWriter;
import eris.compiler.stages.PreDeclarationGenerator;
import eris.compiler.symbol.SymbolTable;

public class ParsedModuleState extends ModuleState {
    public final ModuleNode moduleNode;
    public final SymbolTable definitions;

    public ParsedModuleState(ModuleNode moduleNode, SymbolTable definitions) {
        this.moduleNode = moduleNode;
        this.definitions = definitions;
    }

    public static ParsedModuleState build(BuildModule module, PreParsedModuleState state) throws CompilerError {
        Parser parser = new Parser(module, state.getTokens());
        ModuleNode moduleNode = parser.parse();

        PreDeclarationGenerator preDeclarationGenerator = new PreDeclarationGenerator(module, moduleNode);
        SymbolTable definitions = preDeclarationGenerator.generate();

        new NodeWriter().write(moduleNode);

        return new ParsedModuleState(moduleNode, definitions);
    }

    public ModuleNode getModuleNode() {
        return moduleNode;
    }
}
