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

import java.math.BigInteger;

/**
 * BZOJ1951 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 30, 2019</pre>
 */
public class BZOJ1951Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setTestTime(1000)
                        .setActualSolution(new OJMainSolution(BZOJ1951.class))
                        .setExpectedSolution(new OJMainSolution(Solution.class))
                        .setInputFactory(new Generator())
                        .build().call()
        );
    }

    private static int limit = 10;

    public static class Solution {
        public static void main(String[] args) {
            int mod = 999911659;

            FastIO io = new FastIO(System.in, System.out);
            int n = io.readInt();
            int g = io.readInt();

            BigInteger pow = BigInteger.ZERO;
            for (int i = 1; i <= n; i++) {
                if (n % i != 0) {
                    continue;
                }
                pow = pow.add(composite(n, i));
            }
            io.cache.append(BigInteger.valueOf(g).modPow(pow, BigInteger.valueOf(mod)));
            io.flush();
        }

        public static BigInteger composite(int n, int d) {
            if (n < d) {
                return BigInteger.ZERO;
            }
            BigInteger prod = BigInteger.ONE;
            BigInteger div = BigInteger.ONE;
            while (d != 0) {
                prod = prod.multiply(BigInteger.valueOf(n));
                div = div.multiply(BigInteger.valueOf(d));
                n--;
                d--;
            }
            return prod.divide(div);
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            input.add(nextInt(1, limit));
            input.add(nextInt(1, limit));
            input.end();
            return input;
        }
    }

} 
