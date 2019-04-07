package com.daltao.oj.submit;


import com.daltao.oj.old.submit.poj.POJ2728;
import com.daltao.oj.tool.OJMainSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * POJ2728 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>四月 5, 2019</pre>
 */
public class POJ2728Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setTestTime(1000)
                        .setInputFactory(new Generator())
                        .setExpectedSolution(new OJMainSolution(POJ2728.class))
                        .setActualSolution(new OJMainSolution(com.daltao.oj.submit.POJ2728.class))
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(2, 3);
            input.add(n);
            Set<List<Integer>> set = new HashSet<>();
            while (set.size() < n) {
                List<Integer> list = Arrays.asList(nextInt(1, 3), nextInt(1, 3), nextInt(1, 3));
                set.add(list);
            }
            for (List<Integer> list : set) {
                input.add(String.format("%d %d %d", list.get(0), list.get(1), list.get(2)));
            }
            input.add(0);
            return input.end();
        }
    }

} 
