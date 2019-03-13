package com.daltao.oj.submit;


import com.daltao.oj.tool.OJMainSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * BZOJ1858 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>三月 2, 2019</pre>
 */
public class BZOJ1858Test {

    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setActualSolution(new OJMainSolution(BZOJ1858.class))
                .setExpectedSolution(new OJMainSolution(RightSolution.class))
                .setInputFactory(new InputGenerator())
                .setTestTime(1000)
                .setTimeLimitForEachTestCase(1000)
                .build().call());
    }

    public static class RightSolution {
        public static void main(String[] args) {
            FastIO io = new FastIO(System.in, System.out);
            int n = io.readInt();
            int m = io.readInt();
            int[] data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = io.readInt();
            }
            for (int i = 0; i < m; i++) {
                int c = io.readInt();
                int l = io.readInt();
                int r = io.readInt();
                switch (c) {
                    case 0:
                        command0(data, l, r);
                        break;
                    case 1:
                        command1(data, l, r);
                        break;
                    case 2:
                        command2(data, l, r);
                        break;
                    case 3:
                        System.out.println(command3(data, l, r));
                        break;
                    case 4:
                        System.out.println(command4(data, l, r));
                        break;
                }
            }
        }

        public static void command0(int[] data, int l, int r) {
            Arrays.fill(data, l, r + 1, 0);
        }

        public static void command1(int[] data, int l, int r) {
            Arrays.fill(data, l, r + 1, 1);
        }

        public static void command2(int[] data, int l, int r) {
            for (int i = l; i <= r; i++) {
                data[i] = (data[i] + 1) % 2;
            }
        }

        public static int command3(int[] data, int l, int r) {
            int s = 0;
            for (int i = l; i <= r; i++) {
                s += data[i];
            }
            return s;
        }

        public static int command4(int[] data, int l, int r) {
            int longest = 0;
            for (int i = l; i <= r; i++) {
                int j = i;
                while (j <= r && data[j] == 1) {
                    j++;
                }
                longest = Math.max(longest, j - i);
                i = j;
            }
            return longest;
        }

    }

    static class InputGenerator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 10);
            int m = nextInt(1, 10);
            input.add(n + " " + m);
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < n; i++) {
                data.append(nextInt(0, 1)).append(" ");
            }
            input.add(data.toString());
            for (int i = 0; i < m; i++) {

                int l = nextInt(0, n - 1);
                int r = nextInt(l, n - 1);
                input.add(nextInt(0, 4) + " " + l + " " + r);
            }
            input.end();
            return input;
        }
    }
} 
