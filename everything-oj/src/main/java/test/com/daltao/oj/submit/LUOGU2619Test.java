package test.com.daltao.oj.submit;

import com.daltao.oj.submit.LUOGU2619;
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
 * LUOGU2619 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>八月 25, 2019</pre>
 */
public class LUOGU2619Test {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LUOGU2619.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("D:\\DATABASE\\CODE\\oj-c\\LUOGU2619_OJ.exe")))
                .setInputFactory(new Generator())
                .setTestTime(10000)
                .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int v = nextInt(1, 4);
            int e = v - 1 + v - 1;
            int require = nextInt(0, v - 1);
            input.add(v).add(e).add(require);
            for (int i = 1; i < v; i++) {
                input.add(String.format("%d %d %d %d", nextInt(0, i - 1), i, nextInt(1, 100), 0));
            }
            for (int i = 1; i < v; i++) {
                input.add(String.format("%d %d %d %d", nextInt(0, i - 1), i, nextInt(1, 100), 1));
            }
            return input.end();
        }
    }
} 
