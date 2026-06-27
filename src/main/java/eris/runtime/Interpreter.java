package eris.runtime;

import eris.module.Function;
import eris.module.Instruction;
import eris.module.Module;
import eris.module.constant.Constant;
import eris.module.constant.FunctionReferenceConstant;

import eris.runtime.LoadedModule.ResolvedFunctionReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Interpreter {
    private final LoadedModule entryModule;
    private final ModuleManager manager = new ModuleManager();

    private final List<Object> stack = new ArrayList<>();
    private final Stack<CallFrame> callStack = new Stack<>();
    private boolean halted = false;

    private LoadedModule module;
    private Function function;
    private List<Constant> constants;
    private Instruction[] code;
    private int instructionPointer;
    private int basePointer;

    public Interpreter(Module module) {
        this.entryModule = manager.addModule(module);;
    }

    public void run() {
        Function entryFunction = entryModule.getEntryFunction();
        if (entryFunction == null) {
            throw new RuntimeException("Entry function not found");
        }

        runFromEntryFunction(entryModule, entryFunction);
    }

    public void runFromEntryFunction(LoadedModule module, Function entry) {
        enterFunction(module, entry);

        while (!halted) {
            Instruction instruction = code[instructionPointer];
            System.out.println(">> " + instruction);
            instructionPointer++;

            runInstruction(instruction);
        }

        dumpStack();
    }

    public void enterFunction(LoadedModule module, Function function) {
        if (this.function != null) {
            CallFrame frame = new CallFrame(this.module, this.function, instructionPointer, basePointer);
            callStack.push(frame);
        }

        this.module = module;
        this.function = function;
        this.constants = module.constants;
        this.code = function.code;
        this.instructionPointer = 0;
        this.basePointer = stack.size() - function.numArgs;

        for (int i = 0; i < function.numLocals; i++) {
            stack.add(null);
        }
    }

    public void exitFunction() {
        Object returnValue = stack.getLast();
        while (stack.size() > basePointer) {
            stack.removeLast();
        }
        stack.add(returnValue);

        if (callStack.isEmpty()) {
            throw new RuntimeException("Cannot return from entry function");
        } else {
            CallFrame frame = callStack.pop();

            this.module = frame.module;
            this.function = frame.function;
            this.constants = frame.module.constants;
            this.code = frame.function.code;
            this.instructionPointer = frame.instructionPointer;
            this.basePointer = frame.basePointer;
        }
    }

    public void runInstruction(Instruction instruction) {
        int argument = instruction.argument;

        switch (instruction.opcode) {
            case LOAD_CONST: {
                Constant constant = constants.get(argument);
                stack.add(constant);
                break;
            }

            case LOAD_LOCAL: {
                Object value = stack.get(basePointer + argument);
                stack.add(value);
                break;
            }

            case STORE_LOCAL: {
                Object value = stack.removeLast();
                stack.set(basePointer + argument, value);
                break;
            }

            case CALL: {
                Constant constant = constants.get(argument);
                FunctionReferenceConstant reference = (FunctionReferenceConstant) constant;
                ResolvedFunctionReference resolved = module.resolveFunction(reference);
                enterFunction(resolved.module, resolved.function);
                break;
            }

            case RETURN: {
                exitFunction();
                break;
            }

            case HALT: {
                halted = true;
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
            LoadedModule module,
            Function function,
            int instructionPointer,
            int basePointer) {
    }
}
