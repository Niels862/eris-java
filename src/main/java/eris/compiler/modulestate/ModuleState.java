package eris.compiler.modulestate;

public abstract class ModuleState {
    public String toString() {
        return String.format("<%s>", getClass().getSimpleName());
    }
}
