package eris.compiler.type;

import eris.compiler.CompilerError;
import eris.compiler.ast.NamedTypeNode;
import eris.compiler.ast.NodeVisitor;
import eris.compiler.ast.NullableTypeNode;
import eris.compiler.ast.TypeNode;
import eris.compiler.symbol.ClassSymbol;

public class TypeBuilder extends NodeVisitor<Type> {
    public Type build(TypeNode typeNode) throws CompilerError {
        return typeNode.accept(this);
    }

    @Override
    public Type visit(NamedTypeNode node) throws CompilerError {
        if (node.symbol instanceof ClassSymbol classSymbol) {
            return new ClassType(classSymbol);
        }

        throw new RuntimeException(String.format("Illegal symbol type: %s", node.symbol));
    }

    @Override
    public Type visit(NullableTypeNode node) throws CompilerError {
        return new NullableType(build(node.type));
    }
}
