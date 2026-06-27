package eris.compiler.ir;

import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.ast.NodeVisitor;

public abstract class IntermediateInstruction {
    private int line;
    private int column;

    public abstract <T> T accept(IntermediateInstructionVisitor<T> visitor) throws CompilerError;

    public abstract String toString();

    public void setPosition(int line, int column) {
        assert this.line == 0 && this.column == 0;
        assert line > 0 && column > 0;
        this.line = line;
        this.column = column;
    }

    public CompilerError error(BuildModule module, String message) {
        if (line > 0) {
            return new CompilerError(module, line, column, message);
        } else {
            return new CompilerError(module, message);
        }
    }
}
