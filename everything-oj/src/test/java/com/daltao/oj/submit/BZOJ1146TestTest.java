package com.daltao.oj.submit;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * BZOJ1146 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>七月 3, 2019</pre>
 */
public class BZOJ1146TestTest {
    @Test
    public void test() throws FileNotFoundException {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\taoda\\Documents\\oj-c\\BZOJ1146_oj.exe")))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1146.class)))
                        .setTimeLimitForEachTestCase(3000)
                        .setTestTime(-1)
                        .setFailInputRecord(new FailResultPrinter(new OutputStreamWriter(new FileOutputStream("D:\\DATABASE\\TESTCASE\\code.gen"))))
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 80000);
            int q = nextInt(1, 80000);
            input.add(n).add(q);
            for (int i = 0; i < n; i++) {
                input.add(nextInt(1, n));
            }
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %d", nextInt(1, i - 1), i));
            }
            for (int i = 0; i < q; i++) {
                input.add(String.format("%d %d %d",
                        nextInt(0, n), nextInt(1, n), nextInt(1, n)));
            }
            return input.end();
        }
    }
} 
