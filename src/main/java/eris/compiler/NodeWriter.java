package eris.compiler;

import eris.compiler.ast.*;

import java.util.List;

public class NodeWriter extends NodeVisitor<Void> {
    private int level;
    
    @Override
    public Void defaultHandler(AbstractNode node) {
        throw new UnsupportedOperationException();
    }

    public Void visit(ModuleNode node) {
        write("functions", node.functions);
        return null;
    }

    public Void visit(FunctionNode node) {
        write("name", node.name);
        write("statements", node.statements);
        return null;
    }

    public Void visit(ReturnStatementNode node) {
        if (node.value != null) {
            write("value", node.value);
        }
        return null;
    }

    public Void visit(IntegerNode node) {
        write("value", Integer.toString(node.value));
        return null;
    }

    private void write(String string) {
        writeLevel();
        System.out.println(string);
    }

    private void write(String key, String value) {
        write(key + ": " + value);
    }

    private <T extends AbstractNode> void write(String key, List<T> nodes) {
        write(key + ": [");
        level++;

        for (T node : nodes) {
            write("{");
            level++;
            writeDefault(node);
            node.accept(this);
            level--;
            write("}");
        }

        level--;
        write("]");
    }

    private void write(String key, AbstractNode node) {
        write(key + ": {");
        level++;
        writeDefault(node);
        node.accept(this);
        level--;
        write("}");
    }

    private void writeLevel() {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
    }

    private void writeDefault(AbstractNode node) {
        write("kind", node.getClass().getSimpleName());
    }
}
