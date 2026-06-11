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

%%


<YYINITIAL> {
  {IntegerLiteral}            { return token(TokenKind.NUMBER); }

  {WhiteSpace}                {}
}

<<EOF>>                       { return token(TokenKind.EOF); }

[^]                           { return token(TokenKind.UNRECOGNIZED); }
