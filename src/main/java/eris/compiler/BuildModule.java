package eris.compiler;

import eris.compiler.ast.ClassNode;
import eris.compiler.ast.ModuleNode;
import eris.compiler.ast.NodeWriter;
import eris.compiler.stages.*;
import eris.module.Module;
import eris.module.Class;
import eris.module.Function;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BuildModule {
    public final String name;
    public final Path path;

    public ModuleNode moduleNode;

    public BuildModule(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public void parse() throws CompilerError {
        List<Token> tokens = new ArrayList<>();

        try {
            try (Reader reader = Files.newBufferedReader(path)) {
                eris.compiler.Lexer lexer = new eris.compiler.Lexer(reader);

                Token token;
                do {
                    token = lexer.nextToken();
                    tokens.add(token);
                } while (token.kind != TokenKind.EOF);
            }
        } catch (IOException e) {
            throw new CompilerError(String.format("Could not read %s", path));
        }

        // Here, the tokens can be scanned for exported symbols, which can be set here without dependencies

        Parser parser = new Parser(this, tokens);
        moduleNode = parser.parse();
    }

    public void analyze() throws CompilerError {
        SymbolDeclarer declarer = new SymbolDeclarer(this);
        declarer.declareSymbols();

        SymbolResolver resolver = new SymbolResolver(this);
        resolver.resolveSymbols();

        new NodeWriter().write(moduleNode);
    }

    public Module compile() throws CompilerError {
        BuildFunctionGenerator generator = new BuildFunctionGenerator(this);
        List<BuildFunction> buildFunctions = generator.generate(this.moduleNode);

        ConstantManager constantManager = new ConstantManager();

        List<Class> classes = new ArrayList<>();
        List<Function> functions = new ArrayList<>();

        for (ClassNode classNode : moduleNode.classes) {
            classes.add(new Class(classNode.symbol.name));
        }

        int entryFunctionIndex = -1;
        for (BuildFunction buildFunction : buildFunctions) {
            if (buildFunction.symbol == moduleNode.entrySymbol) {
                entryFunctionIndex = functions.size();
            }

            FunctionCompiler compiler = new FunctionCompiler(buildFunction, constantManager);
            functions.add(compiler.compile());
        }

        return new Module(name, classes, functions, constantManager.getConstants(), entryFunctionIndex);
    }

    public String toString() {
        return String.format("<BuildModule at %s>", path);
    }
}
