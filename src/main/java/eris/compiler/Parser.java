package eris.compiler;

import eris.compiler.ast.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final BuildModule module;
    private final List<Token> tokens;
    private int index = 0;

    public Parser(BuildModule module, List<Token> tokens) {
        this.module = module;
        this.tokens = tokens;
    }

    public ModuleNode parse() throws CompilerError {
        List<FunctionNode> functions = new ArrayList<>();

        while (!atEnd()) {
            FunctionNode function = parseFunction();
            functions.add(function);
        }

        return new ModuleNode(functions);
    }

    FunctionNode parseFunction() throws CompilerError {
        expect(TokenKind.IDENTIFIER); // Return type, discard
        Token name = expect(TokenKind.IDENTIFIER);
        expect(TokenKind.LPAREN);
        expect(TokenKind.RPAREN);

        List<StatementNode> statements = parseStatementBlock();

        return new FunctionNode(name, statements);
    }

    StatementNode parseStatement() throws CompilerError {
        return parseReturnStatement();
    }

    ReturnStatementNode parseReturnStatement() throws CompilerError {
        expect(TokenKind.RETURN);

        if (accept(TokenKind.SEMICOLON) != null) {
            return new ReturnStatementNode(null);
        } else {
            ExpressionNode value = parseExpression();
            expect(TokenKind.SEMICOLON);
            return new ReturnStatementNode(value);
        }
    }

    List<StatementNode> parseStatementBlock() throws CompilerError {
        expect(TokenKind.LBRACE);

        List<StatementNode> statements = new ArrayList<>();
        while (accept(TokenKind.RBRACE) == null) {
            StatementNode statement = parseStatement();
            statements.add(statement);

            if (atEnd()) {
                throw new ParserError(getToken(), "Unexpected end of file before closing brace");
            }
        }

        return statements;
    }

    ExpressionNode parseExpression() throws CompilerError {
        return parseAtom();
    }

    ExpressionNode parseAtom() throws CompilerError {
        return parseInteger();
    }

    IntegerNode parseInteger() throws CompilerError {
        Token token = expect(TokenKind.INTEGER);
        try {
            int value = Integer.parseInt(token.text);
            return new IntegerNode(token, value);
        } catch (NumberFormatException e) {
            throw new ParserError(token, String.format("Invalid integer value: %s", token.text));
        }
    }

    Token accept(TokenKind kind) {
        Token token = getToken();
        if (token.kind == kind) {
            return nextToken();
        }
        return null;
    }

    Token expect(TokenKind kind) throws CompilerError {
        Token token = getToken();
        if (token.kind == kind) {
            return nextToken();
        }
        throw new ParserError(token, String.format("Expected %s, but got %s", kind, token.kind));
    }

    Token nextToken() {
        Token token = getToken();
        if (index < tokens.size() - 1) {
            index++;
        }
        return token;
    }

    Token getToken() {
        return tokens.get(index);
    }

    boolean atEnd() {
        return getToken().kind == TokenKind.EOF;
    }

    public class ParserError extends CompilerError {
        public ParserError(Token token, String message) {
            super(String.format("%s:%d:%d: %s", module.getPath(), token.line, token.column, message));
        }
    }
}
