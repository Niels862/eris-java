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
    private final BasicBlock entryBlock;

    private final TransferFunctionVisitor transfer = new TransferFunctionVisitor();
    private final TypeContext context = TypeContext.instance;
    private final ContextStringVisitor contextBuilder = new ContextStringVisitor();
    private final Map<VariableSymbol, Integer> localValueIndices = new HashMap<>();
    private final Queue<BasicBlock> tasks = new ArrayDeque<>();
    private final Map<BasicBlock, SemanticState> inStates = new HashMap<>();

    public SemanticAnalyzer(BuildModule module, BuildFunction function) {
        this.module = module;
        this.function = function;
        this.entryBlock = function.blocks.getFirst();
    }

    public void analyze() throws CompilerError {
        setupLocalVariables();
        doConvergencePhase();
        doFinalPhase();
    }

    private void setupLocalVariables() {
        for (VariableSymbol symbol : function.parameters) {
            addLocalMapping(symbol);
        }
        for (VariableSymbol symbol : function.locals) {
            addLocalMapping(symbol);
        }
    }

    private void doConvergencePhase() throws CompilerError {
        tasks.add(entryBlock);
        inStates.put(entryBlock, getInitialState());

        while (!tasks.isEmpty()) {
            BasicBlock block = tasks.remove();
            SemanticState inState = inStates.get(block);
            SemanticState outState = analyzeBlock(block, inState);

            switch (block.getTerminator()) {
                case Jump jump -> {
                    joinState(jump.out, outState);
                }

                case Branch branch -> {
                    joinState(branch.thenOut, outState);
                    joinState(branch.elseOut, outState);
                }

                case Return _, Halt _ -> {}

                default -> throw new RuntimeException();
            }
        }
    }

    private void joinState(BasicBlock block, SemanticState newInState) {
        SemanticState inState = inStates.get(block);
        if (inState == null) {
            inStates.put(block, newInState);
            tasks.add(block);
        } else {
            if (inState.join(newInState)) {
                tasks.add(block);
            }
        }
    }

    private void doFinalPhase() throws CompilerError {
        transfer.enableFinalPhase();
        SemanticState inState = inStates.get(entryBlock);
        analyzeBlock(entryBlock, inState);
    }

    private void addLocalMapping(VariableSymbol symbol) {
        localValueIndices.put(symbol, localValueIndices.size());
    }

    private SemanticState analyzeBlock(BasicBlock block, SemanticState inState) throws CompilerError {
        SemanticState state = inState.copy();

        for (IntermediateInstruction instruction : block.instructions) {
            transfer.apply(state, instruction);
        }

        return state;
    }

    private SemanticState getInitialState() {
        Type[] locals = new Type[localValueIndices.size()];
        for (VariableSymbol parameter : function.parameters) {
            int index = localValueIndices.get(parameter);
            locals[index] = parameter.type;
        }
        return new SemanticState(Collections.emptyList(), locals);
    }

    private boolean isAssignable(Type target, Type value) {
        return target == value;
    }

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

        // Joins OTHER into THIS, returns true if modified
        public boolean join(SemanticState other) {
            assert stack.size() == other.stack.size();

            boolean modified = false;
            for (int i = 0; i < stack.size(); i++) {
                if (stack.get(i) != other.stack.get(i)) {
                    throw new UnsupportedOperationException();
                }
            }
            for (int i = 0; i < locals.length; i++) {
                if (locals[i] != other.locals[i]) {
                    throw new UnsupportedOperationException();
                }
            }
            return modified;
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
        private boolean finalPhase = false;

        public void apply(SemanticState state, IntermediateInstruction instruction) throws CompilerError {
            this.state = state;
            instruction.accept(this);

            System.out.printf("State after %s:%n", instruction);
            state.dump();
        }

        @Override
        public Void visit(LoadConstant instruction) throws CompilerError {
            switch (instruction.constant) {
                case Integer ignored
                        -> state.stack.add(context.INT);

                case Boolean ignored
                        -> state.stack.add(context.BOOL);

                case String ignored
                        -> state.stack.add(context.STRING);

                default -> throw new UnsupportedOperationException(instruction.constant.toString());
            }
            return null;
        }

        @Override
        public Void visit(LoadLocal instruction) throws CompilerError {
            Type valueType = state.getLocal(instruction.symbol);
            if (valueType == null) {
                throw instruction.error(module, String.format("%s is not defined", instruction.symbol.name));
            }
            state.stack.add(valueType);
            return null;
        }

        @Override
        public Void visit(StoreLocal instruction) throws CompilerError {
            Type valueType = state.stack.removeLast();
            VariableSymbol symbol = instruction.symbol;
            if (instruction.symbol.type != null) {
                checkType(symbol.type, valueType, instruction);
            } else if (finalPhase) {
                if (instruction.isInitializingAssignment) {
                    symbol.setInferredType(valueType);
                } else {
                    checkType(symbol.getInferredType(), valueType, instruction);
                }
            }
            state.setLocal(symbol, valueType);
            return null;
        }

        @Override
        public Void visit(Pop instruction) throws CompilerError {
            state.stack.removeLast();
            return null;
        }

        @Override
        public Void visit(BinaryOperation instruction) throws CompilerError {
            Type right = state.stack.removeLast();
            Type left = state.stack.removeLast();
            state.stack.add(context.BOOL);
            return null;
        }

        @Override
        public Void visit(Jump instruction) throws CompilerError {
            return null;
        }

        @Override
        public Void visit(Branch instruction) throws CompilerError {
            Type condition = state.stack.removeLast();
            checkType(context.BOOL, condition, instruction);
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
            if (!isAssignable(target, value)) {
                String contextString = contextBuilder.getContextString(instruction);
                StringBuilder sb = new StringBuilder();
                if (contextString != null) {
                    sb.append(contextString).append(": ");
                }
                sb.append(String.format("Cannot assign value of type %s to value of type %s", value, target));
                throw instruction.error(module, sb.toString());
            }
        }

        public void enableFinalPhase() {
            this.finalPhase = true;
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
