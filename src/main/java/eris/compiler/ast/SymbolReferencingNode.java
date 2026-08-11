package eris.compiler.ast;

import eris.compiler.symbol.Symbol;

public interface SymbolReferencingNode {
    Symbol getReferencedSymbol();
}
