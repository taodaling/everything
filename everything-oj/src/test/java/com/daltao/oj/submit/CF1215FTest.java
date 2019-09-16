package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class CF1215FTest {

    @Test
    public void test(){
        Assert.assertTrue(new TestCaseExecutor.Builder()
        .setInputFactory(new Generator())
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(CF1215F.class)))
        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(CF1215F.class)))
                .setTestTime(100).setTimeLimitForEachTestCase(200000).setFailInputRecord((x) -> {})
        .build().call());
    }

    public static class Generator extends RandomFactory {

        @Override
        public Input newInstance() {
            int limit = 400000;
            QueueInput input = new QueueInput();
            int n = nextInt(limit, limit);
            int p = nextInt(limit, limit);
            int M = nextInt(limit, limit);
            int m = nextInt(limit, limit);

            input.add(n).add(p).add(M).add(m);
            for (int i = 0; i < n; i++) {
                int a = nextInt(1, p);
                int b = nextInt(1, p);
                input.add(a).add(b);
            }

            for (int i = 0; i < p; i++) {
                int l = nextInt(1, M);
                int r = nextInt(1, M);
                if (l > r) {
                    int tmp = l;
                    l = r;
                    r = tmp;
                }
                input.add(l).add(r);
            }

            for(int i = 0; i < m; i++){
                int a = nextInt(1, p);
                int b = nextInt(1, p);
                input.add(a).add(b);
            }

            return input.end();
        }
    }
}
