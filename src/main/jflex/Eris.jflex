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

Identifier = [A-Za-z_][A-Za-z0-9_]*

IntegerLiteral = -? (0 | [0-9_]+ | 0x[0-9A-Fa-z_]+ | 0b[01_]+ | 0u[1_]+)
InvalidIntegerLiteral = -? [0-9][A-Za-z0-9_]*

%%


<YYINITIAL> {
  "func"                    { return token(TokenKind.FUNC); }
  "return"                  { return token(TokenKind.RETURN); }
  "if"                      { return token(TokenKind.IF); }
  "else"                    { return token(TokenKind.ELSE); }
  "while"                   { return token(TokenKind.WHILE); }
  "do"                      { return token(TokenKind.DO); }
  "loop"                    { return token(TokenKind.LOOP); }
  "for"                     { return token(TokenKind.FOR); }
  "var"                     { return token(TokenKind.VAR); }
  "true"                    { return token(TokenKind.TRUE); }
  "false"                   { return token(TokenKind.FALSE); }
  "null"                    { return token(TokenKind.NULL); }

  "==="                     { return token(TokenKind.IDEQ_OP); }
  "!=="                     { return token(TokenKind.IDNE_OP); }
  "=="                      { return token(TokenKind.EQ_OP); }
  "!="                      { return token(TokenKind.NE_OP); }
  "->"                      { return token(TokenKind.ARROW); }
  "("                       { return token(TokenKind.LPAREN); }
  ")"                       { return token(TokenKind.RPAREN); }
  "["                       { return token(TokenKind.LBRACKET); }
  "]"                       { return token(TokenKind.RBRACKET); }
  "{"                       { return token(TokenKind.LBRACE); }
  "}"                       { return token(TokenKind.RBRACE); }
  ","                       { return token(TokenKind.COMMA); }
  ";"                       { return token(TokenKind.SEMICOLON); }
  ":"                       { return token(TokenKind.COLON); }
  "="                       { return token(TokenKind.EQ); }
  "?"                       { return token(TokenKind.QMARK); }
  "!"                       { return token(TokenKind.EMARK); }

  {Identifier}              { return token(TokenKind.IDENTIFIER); }
  {IntegerLiteral}          { return token(TokenKind.INTEGER); }
  {InvalidIntegerLiteral}   { return token(TokenKind.INVALID_INTEGER); }

  {WhiteSpace}              {}
}

<<EOF>>                     { return token(TokenKind.EOF); }

[^]                         { return token(TokenKind.UNRECOGNIZED); }
