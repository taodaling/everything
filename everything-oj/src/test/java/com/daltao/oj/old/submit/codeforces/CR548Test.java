package com.daltao.oj.old.submit.codeforces;

import com.daltao.oj.tool.OJMainSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * CR548 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>ÈýÔÂ 24, 2019</pre>
 */
public class CR548Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(new OJMainSolution(BruteSolution.class))
                .setActualSolution(new OJMainSolution(CR548.class))
                .setTestTime(100)
                .build().call());
    }

    public static class BruteSolution {
        public static void main(String[] args) {
            FastIO io = new FastIO(System.in, System.out);
            int n = io.readInt();
            int m = io.readInt();
            int[] data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = io.readInt();
            }
            int c = count(data, 0, new int[n], m);
            System.out.println(c);
        }

        public static int count(int[] data, int i, int[] seq, int k) {
            if (i == data.length) {
                for (int j = 2; j < data.length; j++) {
                    if (seq[j] == seq[j - 2]) {
                        return 0;
                    }
                }
                return 1;
            }

            if (data[i] != -1) {
                seq[i] = data[i];
                return count(data, i + 1, seq, k);
            }

            int total = 0;
            for (int j = 1; j <= k; j++) {
                seq[i] = j;
                total += count(data, i + 1, seq, k);
            }
            return total;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 7);
            int m = nextInt(1, 5);
            input.add(n).add(m);
            for (int i = 0; i < n; i++) {
                if (nextInt(0, 1) == 1) {
                    input.add(-1);
                } else {
                    input.add(nextInt(1, m));
                }
            }
            input.end();
            return input;
        }
    }
} 
