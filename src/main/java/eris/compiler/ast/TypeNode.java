package eris.compiler.ast;

import eris.compiler.Token;

public abstract class TypeNode extends Node {
    TypeNode(Token token) {
        super(token);
    }
}
