package com.daltao.oj.submit;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * BZOJ1013 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 16, 2019</pre>
 */
public class BZOJ1013Test {

    @Test
    public void test(){
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1013.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\taoda\\Documents\\oj-c\\BZOJ1013_oj.exe")))
                .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 2);
            input.add(n);
            for (int i = 0; i <= n; i++) {
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < n; j++) {
                    builder.append(nextInt(-100, 100)).append(' ');
                }
                input.add(builder.toString());
            }
            return input.end();
        }
    }

} 
