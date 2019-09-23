package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class BZOJ1109Test {

    @Test
    public void test(){
        Assert.assertTrue(new TestCaseExecutor.Builder()
        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solver.class)))
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1109.class)))
        .setInputFactory(new Generator())
        .build().call());
    }

    public static class Solver {
        public static void main(String[] args) {
            FastIO io = new FastIO();

            int n = io.readInt();
            int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = io.readInt();
            }
            int mask = (1 << n) - 1;
            int max = 0;
            for (int i = 1; i <= mask; i++) {
                max = Math.max(max, calc(a, i));
            }

            io.cache.append(max);

            io.flush();
        }

        public static int calc(int[] a, int bit) {
            int cnt = 0;
            int pre = 0;
            for (int i = 0; bit != 0; i++, bit = bit >> 1) {
                if (a[i] == pre + 1 && (bit & 1) == 1) {
                    cnt++;
                }
                pre += bit & 1;
            }
            return cnt;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput in = new QueueInput();
            int n = nextInt(1, 5);
            in.add(n);
            for (int i = 1; i <= n; i++) {
                in.add(nextInt(1, n));
            }
            return in.end();
        }
    }
}
