package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.ClassNode;
import eris.compiler.ast.FunctionNode;
import eris.compiler.ast.ModuleNode;
import eris.compiler.symbol.ClassSymbol;
import eris.compiler.symbol.FunctionSymbol;
import eris.compiler.symbol.Symbol;
import eris.compiler.symbol.SymbolTable;

public class PreDeclarationGenerator {
    private final BuildModule module;
    private final ModuleNode moduleNode;

    private final SymbolTable symbolTable = new SymbolTable();

    public PreDeclarationGenerator(BuildModule module, ModuleNode moduleNode) {
        this.module = module;
        this.moduleNode = moduleNode;
    }

    public SymbolTable generate() throws CompilerError {
        for (ClassNode classNode : moduleNode.classes) {
            classNode.symbol = generateClass(classNode);
            define(classNode.symbol);
        }

        for (FunctionNode functionNode : moduleNode.functions) {
            functionNode.symbol = generateFunction(functionNode);
            define(functionNode.symbol);
        }

        return symbolTable;
    }

    private ClassSymbol generateClass(ClassNode node) throws CompilerError {
        return new ClassSymbol(node.name, module, node.line, node.column);
    }

    private FunctionSymbol generateFunction(FunctionNode node) throws CompilerError {
        return new FunctionSymbol(node.name, module, node.line, node.column);
    }

    private void define(Symbol symbol) throws CompilerError {
        if (!symbolTable.defines(symbol.name)) {
            symbolTable.insert(symbol.name, symbol);
        }
    }
}
