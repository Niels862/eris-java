package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.ast.*;

public class SymbolBuilder {
    private final BuildModule module;

    public SymbolBuilder(BuildModule module) {
        this.module = module;
    }

    public ClassSymbol build(ClassNode node) {
        return new ClassSymbol(node.name, module, node.line, node.column);
    }

    public FunctionSymbol build(FunctionNode node) {
        return new FunctionSymbol(node.name, module, node.line, node.column);
    }

    public LocalValueSymbol build(ParameterNode node) {
        return new LocalValueSymbol(node.name, module, node.line, node.column);
    }

    public ValueSymbol build(VariableNode node, Symbol frame) {
        if (frame instanceof ClassSymbol classFrame) {
            return new AttributeValueSymbol(node.name, module, node.line, node.column, classFrame);
        }

        if (frame instanceof FunctionSymbol) {
            return new LocalValueSymbol(node.name, module, node.line, node.column);
        }

        throw new IllegalStateException();
    }
}
