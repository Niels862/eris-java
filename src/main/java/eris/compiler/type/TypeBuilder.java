package eris.compiler.type;

import eris.compiler.CompilerError;
import eris.compiler.Interner;
import eris.compiler.ast.NamedTypeNode;
import eris.compiler.ast.NodeVisitor;
import eris.compiler.ast.NullableTypeNode;
import eris.compiler.ast.TypeNode;
import eris.compiler.symbol.ClassSymbol;

public class TypeBuilder extends NodeVisitor<Type> {
    private final Interner<Type, NullableType> nullableTypes = new Interner<>(NullableType::new, null);

    public Type build(TypeNode typeNode) throws CompilerError {
        return typeNode.accept(this);
    }

    @Override
    public Type visit(NamedTypeNode node) throws CompilerError {
        if (node.symbol instanceof ClassSymbol classSymbol) {
            return classSymbol.valueType;
        }

        throw new RuntimeException(String.format("Illegal symbol type: %s", node.symbol));
    }

    @Override
    public Type visit(NullableTypeNode node) throws CompilerError {
        return nullableTypes.get(build(node.type));
    }
}
