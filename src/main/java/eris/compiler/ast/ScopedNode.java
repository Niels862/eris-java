package eris.compiler.ast;

import eris.compiler.CompilerError;
import eris.compiler.symbol.ScopeHandler;

public interface ScopedNode {
    <T> void acceptChildren(NodeVisitor<T> visitor, ScopeHandler scopeHandler) throws CompilerError;
}
