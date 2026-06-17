package eris.runtime;

import eris.module.Function;
import eris.module.Instruction;
import eris.module.Module;
import eris.module.constant.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Interpreter {
    private final Module entryModule;

    private final List<Object> stack = new ArrayList<>();
    private final Stack<CallFrame> callStack = new Stack<>();

    private Module module;
    private Function function;
    private List<Constant> constants;
    private Instruction[] code;
    private int instructionPointer;

    public Interpreter(Module module) {
        this.entryModule = module;
    }

    public void run() {
        Function entryFunction = entryModule.lookupFunction("main");
        if (entryFunction == null) {
            throw new RuntimeException("Entry function not found");
        }

        runFromEntryFunction(entryModule, entryFunction);
    }

    public void runFromEntryFunction(Module module, Function entry) {
        enterFunction(module, entry);

        while (!callStack.empty()) {
            Instruction instruction = code[instructionPointer];
            instructionPointer++;

            runInstruction(instruction);
        }

        dumpStack();
    }

    public void enterFunction(Module module, Function function) {
        CallFrame frame = new CallFrame(this.module, this.function, constants, code, instructionPointer);
        callStack.push(frame);

        this.module = module;
        this.function = function;
        this.constants = module.constants;
        this.code = function.code;
        this.instructionPointer = 0;
    }

    public void exitFunction() {
        CallFrame frame = callStack.pop();

        this.module = frame.module;
        this.function = frame.function;
        this.constants = frame.constants;
        this.code = frame.code;
        this.instructionPointer = frame.instructionPointer;
    }

    public void runInstruction(Instruction instruction) {
        int argument = instruction.argument;

        switch (instruction.opcode) {
            case LOAD_CONST: {
                Constant constant = constants.get(argument);
                stack.add(constant);
                break;
            }

            case RETURN: {
                exitFunction();
                break;
            }
        }
    }

    private void dumpStack() {
        System.out.println("Stack:");
        for (int i = 0; i < this.stack.size(); i++) {
            System.out.println("[" + i + "]: " + this.stack.get(i));
        }
    }

    private record CallFrame(
            Module module,
            Function function,
            List<Constant> constants,
            Instruction[] code,
            int instructionPointer) {
    }
}
