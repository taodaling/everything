package test.com.daltao.oj.submit;

import com.daltao.oj.submit.CFContest;
import com.daltao.oj.submit.LUOGU1527;
import com.daltao.oj.tool.MainMethod2Runnable;
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
 * CFContest Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>九月 14, 2019</pre>
 */
public class CFContestTest {
    @Test
    public void test(){
        Assert.assertTrue(new TestCaseExecutor.Builder()
        .setInputFactory(new Generator())
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(CFContest.class)))
        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(CFContest.class)))
        .setTestTime(10000).build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            int n = nextInt(1, 100);
            QueueInput input = new QueueInput();
            input.add(n);
            for(int i = 0; i < n; i++)
            {
                newInstance(input, i);
            }
            return input.end();
        }

        public Input newInstance(QueueInput input, int t) {
            int n = nextInt(1, 10);
            input.add(n);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                builder.append(nextInt(0, 9));
            }
            return input.add(builder);
        }
    }
} 
