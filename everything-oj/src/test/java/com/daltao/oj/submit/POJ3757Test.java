package com.daltao.oj.submit;


import com.daltao.oj.tool.OJMainSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * POJ3757 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>四月 5, 2019</pre>
 */
public class POJ3757Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(new OJMainSolution(Solution.class))
                .setActualSolution(new OJMainSolution(POJ3757.class))
                .setTestTime(1000)
                .build().call()
        );
    }

    public static class Solution {
        FastIO io = new FastIO(System.in, System.out);

        public static void main(String[] args) {
            new Solution().solve();
        }

        public void solve() {
            int n = nextInt();
            int k = nextInt();
            double f = nextDouble();
            double[][] m = new double[n][4];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 3; j++) {
                    m[i][j] = nextDouble();
                }
                m[i][3] = speed(m[i]);
            }
            double minCost = dfs(m, 0, new ArrayList<>(), k);
            io.cache.append(String.format("%.4f", minCost * f));
            io.flush();
        }

        public double speed(double[] m) {
            return 1 / (1 / m[0] + 1 / m[1]);
        }

        public double calc(List<double[]> queue) {
            double totalSpeed = 0;
            for (double[] m : queue) {
                totalSpeed += m[3];
            }
            double cost = 0;
            for (double[] m : queue) {
                cost += m[3] / totalSpeed * m[2];
            }
            return cost;
        }

        public double dfs(double[][] m, int i, List<double[]> queue, int k) {
            if (queue.size() == k) {
                return calc(queue);
            }
            if (i >= m.length) {
                return Double.MAX_VALUE;
            }
            double v = dfs(m, i + 1, queue, k);
            queue.add(m[i]);
            v = Math.min(v, dfs(m, i + 1, queue, k));
            queue.remove(queue.size() - 1);
            return v;
        }


        public String nextString() {
            return io.readString();
        }

        public int nextInt() {
            return Integer.parseInt(nextString());
        }

        public double nextDouble() {
            return Double.parseDouble(nextString());
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            int n = nextInt(1, 20);
            int k = nextInt(1, n);
            int f = nextInt(1, n);
            QueueInput input = new QueueInput();
            input.add(String.format("%d %d %d", n, k, f));
            for (int i = 0; i < n; i++) {
                double p = 1 + random.nextDouble();
                double b = 1 + random.nextDouble();
                double c = 1 + random.nextDouble();
                input.add(String.format("%.7f %.7f %.7f", p, b, c));
            }
            return input.end();
        }
    }
} 
