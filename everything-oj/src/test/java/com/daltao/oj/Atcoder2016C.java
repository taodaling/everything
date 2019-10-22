package com.daltao.oj;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.RandomUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;


public class Atcoder2016C {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Task.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .build().call());
    }

    static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            input.add(RandomUtils.getRandomString(random, 'a', 'z', 10));
            input.add(nextInt(1, 100));
            return input.end();
        }
    }

    /**
     * Built using CHelper plug-in Actual solution is at the top
     *
     * @author daltao
     */
    public static class Task {
        public static void main(String[] args) throws Exception {
            Thread thread = new Thread(null, new TaskAdapter(), "daltao", 1 << 27);
            thread.start();
            thread.join();
        }

        static class TaskAdapter implements Runnable {
            @Override
            public void run() {
                InputStream inputStream = System.in;
                OutputStream outputStream = System.out;
                FastInput in = new FastInput(inputStream);
                PrintWriter out = new PrintWriter(outputStream);
                TaskC solver = new TaskC();
                solver.solve(1, in, out);
                out.close();
            }
        }

        static class TaskC {
            public void solve(int testNumber, FastInput in, PrintWriter out) {
                char[] s = new char[100000];
                int n = in.readString(s, 0);
                int k = in.readInt();

                for (int i = 0; i < n && k > 0; i++) {
                    int atLeast = 'z' + 1 - s[i];
                    if (k < atLeast) {
                        continue;
                    }
                    s[i] = 'a';
                    k -= atLeast;
                }

                s[n - 1] = (char) ((s[n - 1] - 'a' + k) % ('z' - 'a' + 1) + 'a');
                out.println(String.valueOf(s, 0, n));
            }

        }

        static class FastInput {
            private final InputStream is;
            private byte[] buf = new byte[1 << 13];
            private int bufLen;
            private int bufOffset;
            private int next;

            public FastInput(InputStream is) {
                this.is = is;
            }

            private int read() {
                while (bufLen == bufOffset) {
                    bufOffset = 0;
                    try {
                        bufLen = is.read(buf);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (bufLen == -1) {
                        return -1;
                    }
                }
                return buf[bufOffset++];
            }

            public void skipBlank() {
                while (next >= 0 && next <= 32) {
                    next = read();
                }
            }

            public int readInt() {
                int sign = 1;

                skipBlank();
                if (next == '+' || next == '-') {
                    sign = next == '+' ? 1 : -1;
                    next = read();
                }

                int val = 0;
                if (sign == 1) {
                    while (next >= '0' && next <= '9') {
                        val = val * 10 + next - '0';
                        next = read();
                    }
                } else {
                    while (next >= '0' && next <= '9') {
                        val = val * 10 - next + '0';
                        next = read();
                    }
                }

                return val;
            }

            public int readString(char[] data, int offset) {
                skipBlank();

                int originalOffset = offset;
                while (next > 32) {
                    data[offset++] = (char) next;
                    next = read();
                }

                return offset - originalOffset;
            }

        }
    }

    public static class Main {

        static Scanner in = new Scanner(System.in);
        static PrintWriter out = new PrintWriter(System.out, false);
        static boolean debug = false;

        static void solve() {
            in = new Scanner(System.in);
            out = new PrintWriter(System.out, false);
            String s = in.next();
            char[] cs = s.toCharArray();
            int n = s.length();
            int k = in.nextInt();
            for (int i = 0; i < n; i++) {
                if (cs[i] == 'a') continue;
                if ('z' - cs[i] + 1 <= k) {
                    k -= 'z' - cs[i] + 1;
                    cs[i] = 'a';
                }
            }

            if (k > 0) {
                k %= 26;
                cs[n - 1] = (char) ((cs[n - 1] - 'a' + k) % 26 + 'a');
            }

            for (int i = 0; i < n; i++) {
                out.print(cs[i]);
            }
            out.println();
        }

        public static void main(String[] args) {
            long start = System.nanoTime();

            solve();
            out.flush();

            long end = System.nanoTime();
            dump((end - start) / 1000000 + " ms");
            in.close();
            out.close();
        }

        static void dump(Object... o) {
            if (debug) System.err.println(Arrays.deepToString(o));
        }
    }

}







