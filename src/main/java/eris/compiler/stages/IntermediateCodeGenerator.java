package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.FunctionNode;
import eris.compiler.ast.ModuleNode;
import eris.compiler.ast.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class IntermediateCodeGenerator {
    private final BuildModule module;
    private final ModuleNode moduleNode;

    private final Queue<Node> taskQueue = new ArrayDeque<>();
    private final List<BuildFunction> functions = new ArrayList<>();

    public IntermediateCodeGenerator(BuildModule module, ModuleNode moduleNode) {
        this.module = module;
        this.moduleNode = moduleNode;
    }

    public List<BuildFunction> generate() throws CompilerError {
        taskQueue.add(moduleNode);

        while (!taskQueue.isEmpty()) {
            Node node = taskQueue.remove();
            BuildFunctionGenerator generator = new BuildFunctionGenerator(module, node, taskQueue);
            BuildFunction function = generator.generate();
            functions.add(function);
        }

        return functions;
    }
}
