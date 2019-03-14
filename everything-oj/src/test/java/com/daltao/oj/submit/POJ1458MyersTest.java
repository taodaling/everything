package com.daltao.oj.submit;


import com.daltao.oj.tool.OJMainSolution;
import com.daltao.test.Checker;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * POJ1458Myers Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>三月 13, 2019</pre>
 */
public class POJ1458MyersTest {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setActualSolution(new OJMainSolution(POJ1458MyersLinearSpace.class))
                .setExpectedSolution(new OJMainSolution(POJ1458DP.class))

                .setInputFactory(new InputGenerator())
                .setTestTime(1000)
                .setTimeLimitForEachTestCase(1000)
                .build().call());
    }

    @Test
    public void testLinearSpace() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setActualSolution(new OJMainSolution(POJ1458MyersLinearSpace.class))
                .setExpectedSolution(new OJMainSolution(POJ1458Myers.class))
                .setInputFactory(new InputGenerator())
                .setCheckerFactory(CustomChecker::new)
                .setTestTime(1000)
                .setTimeLimitForEachTestCase(1000)
                .build().call());
    }

    private static class CustomChecker implements Checker {
        @Override
        public boolean check(Input expected, Input actual, Input input) {
            boolean flag = true;
            while (expected.available() && actual.available()) {
                flag = flag && compare((String) expected.read(), (String) actual.read(), (String) input.read());
            }
            return flag && (expected.available() == actual.available());
        }

        public boolean compare(String expected, String actual, String input) {
            return expected.length() == actual.length()
                    && contain(input, actual);
        }

        private boolean contain(String all, String sub) {
            int n = all.length();
            int m = sub.length();
            int i = 0;
            int j = 0;
            while (i < n && j < m) {
                if (all.charAt(i) == sub.charAt(j)) {
                    i++;
                    j++;
                } else {
                    i++;
                }
            }
            return j == m;
        }
    }

    private static class InputGenerator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 30);
            int m = nextInt(1, 30);
            input.add(RandomUtils.getRandomString(random, 'a', 'z', n));
            input.add(RandomUtils.getRandomString(random, 'a', 'z', m));
            input.end();
            return input;
        }
    }
} 
