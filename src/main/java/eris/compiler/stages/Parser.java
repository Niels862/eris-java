package eris.compiler.stages;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.Token;
import eris.compiler.TokenKind;
import eris.compiler.ast.*;

import java.util.ArrayList;
import java.util.Collections;
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
        Token token = getToken();
        List<FunctionNode> functions = new ArrayList<>();

        while (!atEnd()) {
            FunctionNode function = parseFunction();
            functions.add(function);
        }

        return new ModuleNode(token, functions);
    }

    private FunctionNode parseFunction() throws CompilerError {
        expect(TokenKind.FUNC);
        Token name = expect(TokenKind.IDENTIFIER);
        List<ParameterNode> parameters = parseParameters();
        if (accept(TokenKind.ARROW) != null) {
            expect(TokenKind.IDENTIFIER);
        }
        List<StatementNode> statements = parseStatementBlock();
        return new FunctionNode(name, statements, parameters);
    }

    private List<ParameterNode> parseParameters() throws CompilerError {
        expect(TokenKind.LPAREN);
        if (accept(TokenKind.RPAREN) != null) {
            return Collections.emptyList();
        }

        List<ParameterNode> parameters = new ArrayList<>();

        while (!atEnd()) {
            Token name = expect(TokenKind.IDENTIFIER);
            expect(TokenKind.COLON);
            expect(TokenKind.IDENTIFIER);

            parameters.add(new ParameterNode(name, name.text));

            if (accept(TokenKind.COMMA) == null) {
                expect(TokenKind.RPAREN);
                return parameters;
            }
        }

        throw unexpectedTokenError(getToken(), "parameter declaration");
    }

    private StatementNode parseStatement() throws CompilerError {
        if (matches(TokenKind.RETURN)) {
            return parseReturnStatement();
        }
        if (matches(TokenKind.VAR)) {
            return parseDeclaration();
        }
        throw unexpectedTokenError(getToken(), "statement");
    }

    private DeclarationNode parseDeclaration() throws CompilerError {
        Token token = expect(TokenKind.VAR);
        Token name = expect(TokenKind.IDENTIFIER);

        ExpressionNode initialValue = null;
        if (accept(TokenKind.EQ) != null) {
            initialValue = parseExpression();
        }

        expect(TokenKind.SEMICOLON);
        return new DeclarationNode(name, name.text, initialValue);
    }

    private ReturnStatementNode parseReturnStatement() throws CompilerError {
        Token token = expect(TokenKind.RETURN);

        if (accept(TokenKind.SEMICOLON) != null) {
            return new ReturnStatementNode(token, null);
        } else {
            ExpressionNode value = parseExpression();
            expect(TokenKind.SEMICOLON);
            return new ReturnStatementNode(token, value);
        }
    }

    private List<StatementNode> parseStatementBlock() throws CompilerError {
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

    private ExpressionNode parseExpression() throws CompilerError {
        return parsePostfixExpression();
    }

    private ExpressionNode parsePostfixExpression() throws CompilerError {
        ExpressionNode expression = parseAtom();

        Token token = getToken();
        while (true) {
            if (accept(TokenKind.LPAREN) != null) {
                // TODO: args list
                expect(TokenKind.RPAREN);
                expression = new CallNode(token, expression, Collections.emptyList());
            } else {
                return expression;
            }
        }
    }

    private ExpressionNode parseAtom() throws CompilerError {
        Token token = getToken();
        if (accept(TokenKind.IDENTIFIER) != null) {
            return new IdentifierNode(token, token.text);
        }
        if (accept(TokenKind.INTEGER) != null) {
            return parseInteger(token);
        }
        System.out.printf("%s %s%n", token, TokenKind.INTEGER);
        throw unexpectedTokenError(token, "expression");
    }

    private IntegerNode parseInteger(Token token) throws CompilerError {
        assert token.kind == TokenKind.INTEGER;

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

    private Token accept(TokenKind kind) {
        Token token = getToken();
        if (token.kind == kind) {
            return nextToken();
        }
        return null;
    }

    private Token expect(TokenKind kind) throws CompilerError {
        Token token = getToken();
        if (token.kind == kind) {
            return nextToken();
        }

        throw unexpectedTokenError(token, kind.userString);
    }

    private boolean matches(TokenKind kind) {
        return getToken().kind == kind;
    }

    private Token nextToken() {
        Token token = getToken();
        if (index < tokens.size() - 1) {
            index++;
        }
        return token;
    }

    private Token getToken() {
        return tokens.get(index);
    }

    boolean atEnd() {
        return getToken().kind == TokenKind.EOF;
    }

    private ParserError invalidTokenError(Token token) {
        return new ParserError(token, String.format("Invalid %s: %s", token.kind.userString, token.text));
    }

    private ParserError unexpectedTokenError(Token token, String expected) {
        if (token.kind.invalidVariantOf != null) {
            return invalidTokenError(token);
        }

        String str = String.format("Expected %s, but got %s", expected, token.kind.userString);
        if (token.kind.hasData) {
            return new ParserError(token, str + ": " + token.text);
        } else {
            return new ParserError(token, str);
        }
    }

    public class ParserError extends CompilerError {
        public ParserError(Token token, String message) {
            super(module, token.line, token.column, message);
        }
    }
}
