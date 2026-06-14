package eris.compiler.modulestate;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.stages.Parser;
import eris.compiler.ast.ModuleNode;
import eris.compiler.ast.NodeWriter;

public class ParsedModuleState extends ModuleState {
    private final ModuleNode moduleNode;

    public ParsedModuleState(ModuleNode moduleNode) {
        this.moduleNode = moduleNode;
    }

    public static ParsedModuleState build(BuildModule module, PreParsedModuleState state) throws CompilerError {
        Parser parser = new Parser(module, state.getTokens());
        ModuleNode moduleNode = parser.parse();

        new NodeWriter().write(moduleNode);

        return new ParsedModuleState(moduleNode);
    }

    public ModuleNode getModuleNode() {
        return moduleNode;
    }
}
