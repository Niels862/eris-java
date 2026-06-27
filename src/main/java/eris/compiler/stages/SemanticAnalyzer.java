package eris.compiler.stages;

import eris.compiler.BuildFunction;
import eris.compiler.BuildModule;
import eris.compiler.CompilerError;
import eris.compiler.TypeContext;
import eris.compiler.ir.*;
import eris.compiler.symbol.VariableSymbol;
import eris.compiler.type.Type;

import java.util.*;

public class SemanticAnalyzer {
    private final BuildModule module;
    private final BuildFunction function;

    private final TransferFunctionVisitor transfer = new TransferFunctionVisitor();
    private final TypeContext context = TypeContext.instance;
    private final ContextStringVisitor contextBuilder = new ContextStringVisitor();
    private final Map<VariableSymbol, Integer> localValueIndices = new HashMap<>();

    public SemanticAnalyzer(BuildModule module, BuildFunction function) {
        this.module = module;
        this.function = function;
    }

    public void analyze() throws CompilerError {
        Queue<Task> tasks = new ArrayDeque<>();
        tasks.add(new Task(function.block, getInitialState()));

        while (!tasks.isEmpty()) {
            Task task = tasks.remove();
            SemanticState outState = analyzeBlock(task);
        }
    }

    public void setupLocalVariables() throws CompilerError {
        for (VariableSymbol symbol : function.parameters) {
            localValueIndices.put(symbol, localValueIndices.size());
        }
        for (VariableSymbol symbol : function.locals) {
            localValueIndices.put(symbol, localValueIndices.size());
        }
    }

    private SemanticState analyzeBlock(Task task) throws CompilerError {
        SemanticState state = task.inState.copy();

        for (IntermediateInstruction instruction : task.block.instructions) {
            transfer.apply(state, instruction);
        }

        return state;
    }

    private SemanticState getInitialState() {
        Type[] locals = new Type[localValueIndices.size()];
        return new SemanticState(Collections.emptyList(), locals);
    }

    private boolean isAssignable(Type target, Type value) {
        return target == value;
    }

    private record Task(
            IntermediateBlock block,
            SemanticState inState
    ) {}

    private class SemanticState {
        public List<Type> stack;
        private final Type[] locals;

        public SemanticState(List<Type> stack, Type[] locals) {
            this.stack = stack;
            this.locals = locals;
        }

        public Type getLocal(VariableSymbol symbol) {
            int index = localValueIndices.get(symbol);
            return locals[index];
        }

        public void setLocal(VariableSymbol symbol, Type value) {
            int index = localValueIndices.get(symbol);
            locals[index] = value;
        }

        public SemanticState copy() {
            List<Type> stack = new ArrayList<>(this.stack);
            Type[] locals = this.locals.clone();
            return new SemanticState(stack, locals);
        }

        public void dump() {
            for (int i = 0; i < stack.size(); i++) {
                System.out.printf("[%d]: %s%n", i, stack.get(i));
            }
            System.out.println();
        }
    }

    private class TransferFunctionVisitor extends IntermediateInstructionVisitor<Void> {
        private SemanticState state;

        public void apply(SemanticState state, IntermediateInstruction instruction) throws CompilerError {
            this.state = state;
            instruction.accept(this);

            System.out.printf("State after %s:%n", instruction);
            state.dump();
        }

        @Override
        public Void visit(LoadConstant instruction) throws CompilerError {
            if (instruction.constant instanceof Integer) {
                state.stack.add(context.INT);
            } else {
                throw new UnsupportedOperationException(instruction.constant.toString());
            }
            return null;
        }

        @Override
        public Void visit(LoadLocal instruction) throws CompilerError {
            Type valueType = state.getLocal(instruction.symbol);
            state.stack.add(valueType);
            return null;
        }

        @Override
        public Void visit(StoreLocal instruction) throws CompilerError {
            Type valueType = state.stack.removeLast();
            if (instruction.symbol.type != null) {
                checkType(instruction.symbol.type, valueType, instruction);
            }
            state.setLocal(instruction.symbol, valueType);
            return null;
        }

        @Override
        public Void visit(Call instruction) throws CompilerError {
            for (Type parameterType : instruction.function.type.parameterTypes.reversed()) {
                Type argumentType = state.stack.removeLast();
                checkType(parameterType, argumentType, instruction);
            }
            state.stack.add(instruction.function.type.returnType);
            return null;
        }

        @Override
        public Void visit(Return instruction) throws CompilerError {
            Type returnValueType = state.stack.removeLast();
            checkType(function.symbol.type.returnType, returnValueType, instruction);
            return null;
        }

        @Override
        public Void visit(Halt instruction) throws CompilerError {
            return null;
        }

        private void checkType(
                Type target, Type value,
                IntermediateInstruction instruction) throws CompilerError {
            System.out.printf("%s <- %s: %b%n", target, value, isAssignable(target, value));
            if (!isAssignable(target, value)) {
                String contextString = contextBuilder.getContextString(instruction);
                StringBuilder sb = new StringBuilder();
                if (contextString != null) {
                    sb.append(contextString).append(": ");
                }
                sb.append(String.format("Cannot use %s as %s", value, target));
                throw instruction.error(module, sb.toString());
            }
        }
    }

    private class ContextStringVisitor extends IntermediateInstructionVisitor<String> {
        public String getContextString(IntermediateInstruction instruction) {
            try {
                return instruction.accept(this);
            } catch (CompilerError e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String defaultHandler(IntermediateInstruction instruction) throws CompilerError {
            return null;
        }

        @Override
        public String visit(Call instruction) {
            return String.format("In call to %s", instruction.function.name);
        }

        @Override
        public String visit(Return instruction) {
            return String.format("In return from %s", function.symbol.name);
        }
    }
}
