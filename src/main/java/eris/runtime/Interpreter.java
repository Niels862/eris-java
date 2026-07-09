package eris.runtime;

import eris.module.Function;
import eris.module.Instruction;
import eris.module.Module;
import eris.module.constant.Constant;
import eris.module.constant.FunctionReferenceConstant;

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
    private Object[] resolvedConstants;
    private Instruction[] code;
    private int instructionPointer;
    private int basePointer;

    public Interpreter(Module module) {
        this.entryModule = manager.addModule(module);;
    }

    public void run() {
        LoadedFunction entryFunction = entryModule.entryFunction;
        if (entryFunction == null) {
            throw new RuntimeException("Entry function not found");
        }

        runFromEntryFunction(entryFunction);
    }

    public void runFromEntryFunction(LoadedFunction entryFunction) {
        enterFunction(entryFunction);

        while (!halted) {
            Instruction instruction = code[instructionPointer];
            System.out.println(">> " + instruction);
            instructionPointer++;

            runInstruction(instruction);
        }

        dumpStack();
    }

    public void enterFunction(LoadedFunction loadedFunction) {
        if (this.function != null) {
            CallFrame frame = new CallFrame(this.module, this.function, instructionPointer, basePointer);
            callStack.push(frame);
        }

        this.module = loadedFunction.module;
        this.function = loadedFunction.function;
        this.constants = module.constants;
        this.resolvedConstants = module.resolvedConstants;
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
            this.resolvedConstants = frame.module.resolvedConstants;
            this.code = frame.function.code;
            this.instructionPointer = frame.instructionPointer;
            this.basePointer = frame.basePointer;
        }
    }

    public void runInstruction(Instruction instruction) {
        int argument = instruction.argument;

        switch (instruction.opcode) {
            case LOAD_CONST -> {
                stack.add(resolvedConstants[argument]);
            }

            case LOAD_NULL -> {
                stack.add(null);
            }

            case LOAD_LOCAL -> {
                Object value = stack.get(basePointer + argument);
                stack.add(value);
            }

            case STORE_LOCAL -> {
                Object value = stack.removeLast();
                stack.set(basePointer + argument, value);
            }

            case POP -> {
                stack.removeLast();
            }

            case DUP -> {
                stack.add(stack.getLast());
            }

            case NEW -> {
                stack.add(null);
            }

            case EQ -> {
                Object value1 = stack.removeLast();
                Object value2 = stack.removeLast();
                stack.add(value1 == value2 ? 1 : 0);
            }

            case NE -> {
                Object value1 = stack.removeLast();
                Object value2 = stack.removeLast();
                stack.add(value1 != value2 ? 1 : 0);
            }

            case JUMP -> {
                instructionPointer += argument;
            }

            case BRANCH_IF_TRUE -> {
                Object value = stack.removeLast();
                if (value instanceof Integer integer) {
                    if (integer == 1) {
                        instructionPointer += argument;
                    }
                } else {
                    throw new RuntimeException(value.getClass().toString());
                }
            }

            case BRANCH_IF_FALSE -> {
                Object value = stack.removeLast();
                if (value instanceof Integer integer) {
                    if (integer == 0) {
                        instructionPointer += argument;
                    }
                } else {
                    throw new RuntimeException(value.getClass().toString());
                }
            }

            case CALL -> {
                Constant constant = constants.get(argument);
                FunctionReferenceConstant reference = (FunctionReferenceConstant) constant;
                enterFunction(module.resolveFunction(reference));
            }

            case RETURN -> {
                exitFunction();
            }

            case HALT -> {
                halted = true;
            }

            default -> throw new UnsupportedOperationException("Unsupported instruction opcode: " + instruction.opcode);
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
