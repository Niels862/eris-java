package eris.compiler.ast;

import eris.compiler.CompilerError;

import java.util.List;

public class NodeWriter extends NodeVisitor<Void> {
    private int level;

    public void write(Node node) throws CompilerError {
        writeWithPrefix("", node);
    }

    @Override
    public Void defaultHandler(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visit(ModuleNode node) throws CompilerError {
        write("functions", node.functions);
        return null;
    }

    @Override
    public Void visit(FunctionNode node) throws CompilerError {
        write("name", node.name);
        write("statements", node.statements);
        write("parameters", node.parameters);
        write("returnType", node.returnType);
        return null;
    }

    @Override
    public Void visit(ParameterNode node) throws CompilerError {
        write("name", node.name);
        return null;
    }

    @Override
    public Void visit(VariableNode node) throws CompilerError {
        write("name", node.name);
        write("initialValue", node.initialValue);
        write("type", node.type);
        return null;
    }

    @Override
    public Void visit(ReturnStatementNode node) throws CompilerError {
        write("value", node.value);
        return null;
    }

    @Override
    public Void visit(CallNode node) throws CompilerError {
        write("function", node.function);
        write("arguments", node.arguments);
        return null;
    }

    @Override
    public Void visit(IdentifierNode node) throws CompilerError {
        write("name", node.name);
        return null;
    }

    @Override
    public Void visit(IntegerNode node) {
        write("value", Integer.toString(node.value));
        return null;
    }

    @Override
    public Void visit(NamedTypeNode node) throws CompilerError {
        write("name", node.name);
        return null;
    }

    private void write(String string) {
        writeLevel();
        System.out.println(string);
    }

    private void write(String key, String value) {
        write(key + ": " + value);
    }

    private <T extends Node> void write(String key, List<T> nodes) throws CompilerError {
        if (nodes.isEmpty()) {
            write(key + ": []");
            return;
        }

        write(key + ": [");
        level++;

        for (T node : nodes) {
            writeWithPrefix("", node);
        }

        level--;
        write("]");
    }

    private void write(String key, Node node) throws CompilerError {
        writeWithPrefix(key + ": ", node);
    }

    private void writeWithPrefix(String prefix, Node node) throws CompilerError {
        if (node == null) {
            write(prefix + "null");
        } else {
            write(prefix + "{");
            level++;
            writeDefault(node);
            node.accept(this);
            level--;
            write("}");
        }
    }

    private void writeLevel() {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
    }

    private void writeDefault(Node node) {
        write(node.getClass().getSimpleName() + " at " + node.line + ":" + node.column);
    }
}
