package com.daltao.oj.submit;


import com.daltao.oj.tool.OJMainSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

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
                .setActualSolution(new OJMainSolution(POJ1458Myers.class))
                .setExpectedSolution(new OJMainSolution(POJ1458DP.class))
                .setInputFactory(new InputGenerator())
                .setTestTime(1000)
                .setTimeLimitForEachTestCase(1000)
                .build().call());
    }

    private static class InputGenerator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 100);
            int m = nextInt(1, 100);
            input.add(RandomUtils.getRandomString(random, 'a', 'z', n));
            input.add(RandomUtils.getRandomString(random, 'a', 'z', m));
            input.end();
            return input;
        }
    }
} 
