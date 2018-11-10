package com.daltao.test;

import com.daltao.common.Factory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class TestCaseExecutor implements Callable<Boolean> {
    private final Factory<Function<Input, Input>> actualSolution;
    private final Factory<Function<Input, Input>> expectedSolution;
    private final Factory<Input> inputFactory;
    private final Factory<Checker> checkerFactory;
    private Consumer<Input[]> failInputRecord;
    private Appendable debugOutput;
    private int testTime;

    private TestCaseExecutor(Factory<Function<Input, Input>> actualSolution, Factory<Function<Input, Input>> expectedSolution, Factory<Input> inputFactory, Factory<Checker> checkerFactory, Consumer<Input[]> failInputRecord, Appendable debugOutput, int testTime) {
        this.actualSolution = actualSolution;
        this.expectedSolution = expectedSolution;
        this.inputFactory = inputFactory;
        this.checkerFactory = checkerFactory;
        this.failInputRecord = failInputRecord;
        this.debugOutput = debugOutput;
        this.testTime = testTime;
    }

    @Override
    public Boolean call() {
        boolean result = true;
        for (int i = 0; i < testTime; i++) {
            MultiDirectionInput input = new MultiDirectionInput(inputFactory.newInstance(), 4);
            MultiDirectionInput output1 = null;
            MultiDirectionInput output2 = null;

            //Prompt

            try {
                Input example = input.getInput(3);
                debugOutput.append("Input: \n\n");
                for (int j = 0; j < 10 && example.available(); j++) {
                    debugOutput.append(Objects.toString(example.read())).append("\n");
                }
                if (example.available()) {
                    debugOutput.append("...\n");
                }
                debugOutput.append("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            try {
                output1 = new MultiDirectionInput(actualSolution.newInstance().apply(input.getInput(1)), 2);
                output2 = new MultiDirectionInput(actualSolution.newInstance().apply(input.getInput(2)), 2);
            } catch (Exception t) {
                result = false;
                output1 = new MultiDirectionInput(EmptyInput.getInstance(), 1);
                output2 = new MultiDirectionInput(EmptyInput.getInstance(), 1);
            }

            result = result &&
                    checkerFactory.newInstance().check(
                            output1.getInput(1), output2.getInput(1)
                    );

            if (!result) {
                failInputRecord.accept(new Input[]{input.getInput(0), output1.getInput(0), output2.getInput(0)});
                break;
            }

            try {
                debugOutput.append("Pass test : ").append(String.valueOf(i)).append("\n\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public static class Builder implements com.daltao.common.Builder<TestCaseExecutor> {
        private Factory<Function<Input, Input>> actualSolution;
        private Factory<Function<Input, Input>> expectedSolution;
        private Factory<Input> inputFactory;
        private Factory<Checker> checkerFactory = EqualChecker::getInstance;
        private Consumer<Input[]> failInputRecord = new FailResultPrinter(System.out);
        private Appendable debugOutput = System.out;
        private int testTime = 100;

        public Builder setActualSolution(Factory<Function<Input, Input>> actualSolution) {
            this.actualSolution = actualSolution;
            return this;
        }

        public Builder setExpectedSolution(Factory<Function<Input, Input>> expectedSolution) {
            this.expectedSolution = expectedSolution;
            return this;
        }

        public Builder setInputFactory(Factory<Input> inputFactory) {
            this.inputFactory = inputFactory;
            return this;
        }

        public Builder setCheckerFactory(Factory<Checker> checkerFactory) {
            this.checkerFactory = checkerFactory;
            return this;
        }

        public Builder setTestTime(int testTime) {
            this.testTime = testTime;
            return this;
        }

        public Builder setFailInputRecord(Consumer<Input[]> failInputRecord) {
            this.failInputRecord = failInputRecord;
            return this;
        }

        public Builder setDebugOutput(Appendable debugOutput) {
            this.debugOutput = debugOutput;
            return this;
        }

        @Override
        public TestCaseExecutor build() {
            return new TestCaseExecutor(actualSolution, expectedSolution, inputFactory, checkerFactory, failInputRecord, debugOutput, testTime);
        }
    }

}