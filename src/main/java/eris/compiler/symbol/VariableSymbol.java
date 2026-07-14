package eris.compiler.symbol;

import eris.compiler.BuildModule;
import eris.compiler.type.Type;

public class VariableSymbol extends Symbol {
    private int slotIndex = -1;
    private boolean isDeclared = false;
    private Type type;

    public VariableSymbol(String name, BuildModule module, int line, int column) {
        super(name, module, line, column);
    }

    public void setMeta(Type type) {
        this.type = type;
    }

    public int getSlotIndex() {
        assert slotIndex != -1;
        return slotIndex;
    }

    public void setSlotIndex(int slotIndex) {
        assert slotIndex >= 0;
        assert this.slotIndex == -1;
        this.slotIndex = slotIndex;
    }

    public boolean isDeclared() {
        return isDeclared;
    }

    public void setDeclared() {
        assert !isDeclared;
        isDeclared = true;
    }

    public Type getType() {
        assert type != null;
        return type;
    }

    @Override
    public String toString() {
        return String.format("<Variable %s>", name);
    }
}
