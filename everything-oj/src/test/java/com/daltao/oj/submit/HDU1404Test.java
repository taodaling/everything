package com.daltao.oj.submit;

import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HDU1404Test {

    @Test
    public void test(){
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setTestTime(1000)
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\HDU1404.exe")))
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\_HDU1404.exe")))
                .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            for (int i = 0; i < 1; i++) {
                input.add(String.valueOf(RandomUtils.getRandomCharacterSequence(random, '0', '9', nextInt(1, 6))));
            }
            return input.end();
        }
    }
}
