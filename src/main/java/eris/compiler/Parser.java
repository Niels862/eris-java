package eris.compiler;

import eris.compiler.ast.ASTNode;

import java.util.List;

public class Parser {
    BuildModule module;
    List<Token> tokens;

    public Parser(BuildModule module, List<Token> tokens) {
        this.module = module;
        this.tokens = tokens;
    }

    ASTNode parse() throws CompilerError {
        return null;
    }
}
