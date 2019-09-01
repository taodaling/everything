package com.daltao.oj.submit;

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
 * LUOGU4292 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>八月 31, 2019</pre>
 */
public class LUOGU4292Test {
    @Test
    public void test(){
        Assert.assertTrue(new TestCaseExecutor.Builder()
        .setInputFactory(new Generator())
        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("D:\\DATABASE\\CODE\\oj-c\\LUOGU4292_OJ.exe")))
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LUOGU4292.class)))
        .setTestTime(100).build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(2, 100000);
            int l = nextInt(1, n - 1);
            int r = nextInt(1, n - 1);
            if (l > r) {
                int tmp = l;
                l = r;
                r = tmp;
            }
            input.add(n).add(l).add(r);
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %d %d", nextInt(1, i - 1), i, nextInt(0, 10)));
            }
            return input.end();
        }
    }
} 
