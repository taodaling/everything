package com.daltao.oj.old.submit.bzoj;


import com.daltao.oj.tool.OJMainSolution;
import com.daltao.template.FastIO;
import com.daltao.template.Mathematics;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * BZOJ2154 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 27, 2019</pre>
 */
public class BZOJ2154Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setExpectedSolution(new OJMainSolution(Solution.class))
                        .setActualSolution(new OJMainSolution(BZOJ2154.class))
                        .setTestTime(10000)
                        .build().call()
        );
    }


    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO(System.in, System.out);
            int n = io.readInt();
            int m = io.readInt();
            int mod = 20101009;
            long sum = 0;
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {
                    sum += lcm(i, j);
                    sum %= mod;
                }
            }
            io.cache.append(sum);
            io.flush();
        }

        public static long lcm(int a, int b) {
            int gcd = Mathematics.gcd(a, b);
            return (long) a * b / gcd;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 300);
            int m = nextInt(1, 300);
            input.add(String.format("%d %d", n, m));
            input.end();
            return input;
        }
    }

} 
