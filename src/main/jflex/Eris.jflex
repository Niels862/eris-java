package eris.compiler;

%%

%unicode
%line
%column

%class Lexer
%public

%function nextToken
%type Token

%{
    private Token token(TokenKind kind) {
        return new Token(kind, yytext(), yyline + 1, yycolumn + 1);
    }
%}

WhiteSpace = [ \t\r|\n|\r\n]

IntegerLiteral = 0 | [1-9][0-9]*
Identifier = [A-Za-z_][A-Za-z0-9_]*

%%


<YYINITIAL> {
  "return"                  { return token(TokenKind.RETURN); }

  "("                       { return token(TokenKind.LPAREN); }
  ")"                       { return token(TokenKind.RPAREN); }
  "["                       { return token(TokenKind.LBRACKET); }
  "]"                       { return token(TokenKind.RBRACKET); }
  "{"                       { return token(TokenKind.LBRACE); }
  "}"                       { return token(TokenKind.RBRACE); }
  ","                       { return token(TokenKind.COMMA); }
  ";"                       { return token(TokenKind.SEMICOLON); }

  {IntegerLiteral}          { return token(TokenKind.INTEGER); }
  {Identifier}              { return token(TokenKind.IDENTIFIER); }

  {WhiteSpace}              {}
}

<<EOF>>                     { return token(TokenKind.EOF); }

[^]                         { return token(TokenKind.UNRECOGNIZED); }
