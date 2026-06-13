package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.TokenKind;
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
        Token token = expect(TokenKind.RETURN);

        if (accept(TokenKind.SEMICOLON) != null) {
            return new ReturnStatementNode(token, null);
        } else {
            ExpressionNode value = parseExpression();
            expect(TokenKind.SEMICOLON);
            return new ReturnStatementNode(token, value);
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
            String text = token.text;
            boolean negative = text.startsWith("-");
            if (negative) {
                text = text.substring(1);
            }

            int base;
            if (text.startsWith("0b")) {
                base = 2;
                text = text.substring(2);
            } else if (text.startsWith("0x")) {
                base = 16;
                text = text.substring(2);
            } else if (text.startsWith("0u")) {
                base = 1;
                text = text.substring(2);
            } else {
                base = 10;
            }

            text = text.replace("_", "");

            int value;
            if (base == 1) {
                value = text.length();
                if (negative) {
                    value = -value;
                }
            } else {
                if (negative) {
                    text = "-" + text;
                }
                value = Integer.parseInt(text, base);
            }

            return new IntegerNode(token, value);
        } catch (NumberFormatException e) {
            throw invalidTokenError(token);
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

        if (token.kind.invalidVariantOf == kind) {
            throw invalidTokenError(token);
        }

        String str = String.format("Expected %s, but got %s", kind.userString, token.kind.userString);
        if (token.kind.hasData) {
            throw new ParserError(token, str + ": " + token.text);
        } else {
            throw new ParserError(token, str);
        }
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

    private ParserError invalidTokenError(Token token) {
        return new ParserError(token, String.format("Invalid %s: %s", token.kind.userString, token.text));
    }
}
