package com.daltao.oj.submit;

import com.daltao.oj.submit.LUOGU4512;
import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 * LUOGU4512 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>九月 4, 2019</pre>
 */
public class LUOGU4512Test {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
        .setInputFactory(new Generator())
        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("D:\\DATABASE\\CODE\\oj-c\\LUOGU4512_OJ.exe")))
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LUOGU4512.class)))
        .build().call());
    }


    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(2, 2);
            int m = nextInt(1, n - 1);
            input.add(n).add(m);
            StringBuilder a = new StringBuilder();
            StringBuilder b = new StringBuilder();
            for (int i = 0; i <= n; i++) {
                a.append(nextInt(0, 10)).append(' ');
            }
            for (int i = 0; i <= m; i++) {
                b.append(nextInt(0, 10)).append(' ');
            }
            input.add(a.toString()).add(b.toString());
            return input.end();
        }
    }

} 
