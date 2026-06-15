package eris.compiler.ir;

import eris.module.constant.Constant;

public class LoadConstant extends IntermediateInstruction {
    public final Constant constant;

    public LoadConstant(Constant constant) {
        this.constant = constant;
    }

    public String toString() {
        return "LOAD_CONSTANT " + constant;
    }
}
