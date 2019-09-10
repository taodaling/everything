package com.daltao.test;

import com.daltao.common.Factory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private long timeLimitForEachTestCase;
    private static MessageFormat passPrompt = new MessageFormat("Pass test ({0} ms / {2} ms): {1}\n\n");

    private TestCaseExecutor(Factory<Function<Input, Input>> actualSolution, Factory<Function<Input, Input>> expectedSolution, Factory<Input> inputFactory,
            Factory<Checker> checkerFactory, Consumer<Input[]> failInputRecord, Appendable debugOutput, int testTime, long timeLimitForEachTestCase) {
        this.actualSolution = actualSolution;
        this.expectedSolution = expectedSolution;
        this.inputFactory = inputFactory;
        this.checkerFactory = checkerFactory;
        this.failInputRecord = failInputRecord;
        this.debugOutput = debugOutput;
        this.testTime = testTime;
        this.timeLimitForEachTestCase = timeLimitForEachTestCase;
    }

    @Override
    public Boolean call() {
        boolean result = true;
        for (int i = 1; i != testTime; i++) {
            MultiDirectionInput input = new MultiDirectionInput(inputFactory.newInstance(), 5);
            input.dispatchAll();
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

            System.gc();
            long expBeginTime = System.currentTimeMillis();
            long expTakeTime = 0;
            try {
                output1 = new MultiDirectionInput(executorService.submit(() -> expectedSolution.newInstance().apply(input.getInput(1)))
                        .get(), 2);
                expTakeTime = System.currentTimeMillis() - expBeginTime;
            } catch (Exception t) {
                t.printStackTrace();
                try {
                    debugOutput.append("Expect solution exceeded time limit\n\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = false;
                output1 = new MultiDirectionInput(EmptyInput.getInstance(), 1);
            }

            System.gc();
            long beginTime = System.currentTimeMillis();
            long takeTime = 0;
            try {
                output2 = new MultiDirectionInput(executorService.submit(() -> actualSolution.newInstance().apply(input.getInput(2)))
                        .get(timeLimitForEachTestCase, TimeUnit.MILLISECONDS), 2);
                takeTime = System.currentTimeMillis() - beginTime;
            } catch (Exception t) {
                t.printStackTrace();
                try {
                    debugOutput.append("Actual solution exceeded time limit\n\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = false;
                output2 = new MultiDirectionInput(EmptyInput.getInstance(), 1);
            }

            try {
                MultiDirectionInput finalOutput = output1;
                MultiDirectionInput finalOutput1 = output2;
                result = result && executorService.submit(() -> checkerFactory.newInstance().check(finalOutput.getInput(1), finalOutput1.getInput(1), input.getInput(4)))
                        .get(timeLimitForEachTestCase, TimeUnit.MILLISECONDS);
            } catch (Exception t) {
                t.printStackTrace();
                try {
                    debugOutput.append("Checker exceeded time limit\n\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = false;
                executorService.shutdownNow();
            }

            if (!result) {
                failInputRecord.accept(new Input[] { input.getInput(0), output1.getInput(0), output2.getInput(0) });
                break;
            }

            try {
                debugOutput.append(passPrompt.format(new Object[]{takeTime, i, expTakeTime}));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        executorService.shutdownNow();
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
        private long timeLimitForEachTestCase = 3000;

        public static Builder newBuilder(){
            return new Builder();
        }

        public Builder setTimeLimitForEachTestCase(long timeLimitForEachTestCase) {
            this.timeLimitForEachTestCase = timeLimitForEachTestCase;
            return this;
        }

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
            return new TestCaseExecutor(actualSolution, expectedSolution, inputFactory, checkerFactory, failInputRecord, debugOutput, testTime,
                    timeLimitForEachTestCase);
        }
    }

}
