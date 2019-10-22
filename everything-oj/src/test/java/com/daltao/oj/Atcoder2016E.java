package com.daltao.oj;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;


public class Atcoder2016E {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Task.class)))
                .setTestTime(10000).build().call());
    }

    static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int m = nextInt(1, 5);
            int n = nextInt(1, 5);
            input.add(m).add(n);
            int q = nextInt(1, 6);
            input.add(q);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < q; i++) {
                builder.append(nextInt(1, n)).append(' ');
            }
            input.add(builder.toString());
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
                TaskE solver = new TaskE();
                solver.solve(1, in, out);
                out.close();
            }
        }
        static class TaskE {
            public void solve(int testNumber, FastInput in, PrintWriter out) {
                int m = in.readInt();
                int n = in.readInt();
                int q = in.readInt();
                int[] seq = new int[q + 1];
                int[] last = new int[q + 1];
                int[] reg = new int[n + 1];
                for (int i = 1; i <= q; i++) {
                    seq[i] = in.readInt();
                    last[i] = reg[seq[i]];
                    reg[seq[i]] = i;
                }

                ArrayList<Integer> perms = new ArrayList<>();
                List<Integer> cnts = new ArrayList<>();
                Constraint[] constraints = new Constraint[q + 1];
                List<Constraint> wait = new ArrayList<>(q);
                boolean[] used = new boolean[n + 1];
                boolean flag = true;
                int nextValue = 1;

                for (int i = q; i >= 1; i--) {
                    int v = seq[i];
                    if (used[v]) {
                        continue;
                    }
                    used[v] = true;
                    flag = flag && nextValue == v;
                    nextValue++;
                }

                Arrays.fill(used, false);
                if (flag) {
                    out.println("Yes");
                    return;
                }

                for (int i = q; i >= 1; i--) {
                    int v = seq[i];
                    if (used[v]) {
                        continue;
                    }
                    for (int j = 0, until = wait.size(); j < until; j++) {
                        Constraint c = wait.get(j);
                        c.num = v;
                        c.require = m - j;
                        constraints[c.index] = c;
                    }

                    used[v] = true;
                    wait.clear();
                    for (int j = i; j >= 1; j = last[j]) {
                        Constraint c = new Constraint();
                        c.index = j;
                        wait.add(c);
                    }
                    perms.add(v);
                    cnts.add(wait.size());
                }

                if (cnts.get(0) < m) {
                    out.println("No");
                    return;
                }

                int[] cnt = new int[n + 1];

                boolean valid = true;
                List<Integer> sortedPerm = (List<Integer>) perms.clone();
                sortedPerm.sort(Comparator.naturalOrder());
                for (int i = 0; i < sortedPerm.size(); i++) {
                    if (i + 1 != sortedPerm.get(i)) {
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    for (int i = perms.size() - 1; i >= 0; i--) {
                        if (i < perms.size() - 1 && perms.get(i + 1) - 1 != perms.get(i)) {
                            break;
                        }
                        cnt[perms.get(i)] = m;
                    }
                }

                for (int i = 1; i <= q; i++) {
                    cnt[seq[i]]++;
                    if (constraints[i] != null) {
                        if (cnt[constraints[i].num] < constraints[i].require) {
                            out.println("No");
                            return;
                        }
                    }
                }

                out.println("Yes");
                return;
            }

        }
        static class Constraint {
            int index;
            int num;
            int require;

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

        }
    }




    public static class Main {
        public static void main(String[] args) {
            InputReader in = new InputReader(System.in);
            PrintWriter out = new PrintWriter(System.out);

            int n = in.nextInt();
            int m = in.nextInt();
            int q = in.nextInt();
            int[] a = in.nextInts(q);
            for (int i = 0; i < q; i++) {
                a[i]--;
            }
            int[] ord = makeord(m, a);
            out.println(isOK(n, m, a, ord) ? "Yes" : "No");
            out.flush();
        }

        private static int[] makeord(int m, int[] a) {
            boolean[] seen = new boolean[m];
            int[] ord = new int[m];
            int oi = 0;
            for (int i = a.length - 1; i >= 0; i--) {
                if (!seen[a[i]]) {
                    seen[a[i]] = true;
                    ord[oi++] = a[i];
                }
            }
            for (int i = 0; i < m; i++) {
                if (!seen[i]) {
                    ord[oi++] = i;
                }
            }
            return ord;
        }

        private static boolean isOK(int n, int m, int[] a, int[] ord) {
            int[] iord = new int[m];
            Arrays.fill(iord, -1);
            for (int i = 0; i < ord.length; i++) {
                iord[ord[i]] = i;
            }
            int on = ord.length;
            int[] cnt = new int[on + 1];
            cnt[0] = n;
            for (int i = a.length - 1; i >= 0; i--) {
                int th = iord[a[i]];
                if (cnt[th] >= 1) {
                    cnt[th]--;
                    cnt[th + 1]++;
                }
            }

            for (int i = 0; i <= on; i++) {
                if (cnt[i] >= 1) {
                    int[] ord2 = new int[m];
                    int oi = 0;
                    boolean[] seen = new boolean[m];
                    for (int j = 0; j < i; j++) {
                        ord2[oi++] = ord[j];
                        seen[ord[j]] = true;
                    }
                    for (int j = 0; j < m; j++) {
                        if (!seen[j]) {
                            ord2[oi++] = j;
                        }
                    }
                    return Arrays.equals(ord, ord2);
                }
            }
            throw new RuntimeException("nyan");
        }

        static class InputReader {
            private InputStream stream;
            private byte[] buf = new byte[1024];
            private int curChar;
            private int numChars;

            public InputReader(InputStream stream) {
                this.stream = stream;
            }

            private int[] nextInts(int n) {
                int[] ret = new int[n];
                for (int i = 0; i < n; i++) {
                    ret[i] = nextInt();
                }
                return ret;
            }


            private int[][] nextIntTable(int n, int m) {
                int[][] ret = new int[n][m];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        ret[i][j] = nextInt();
                    }
                }
                return ret;
            }

            private long[] nextLongs(int n) {
                long[] ret = new long[n];
                for (int i = 0; i < n; i++) {
                    ret[i] = nextLong();
                }
                return ret;
            }

            private long[][] nextLongTable(int n, int m) {
                long[][] ret = new long[n][m];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        ret[i][j] = nextLong();
                    }
                }
                return ret;
            }

            private double[] nextDoubles(int n) {
                double[] ret = new double[n];
                for (int i = 0; i < n; i++) {
                    ret[i] = nextDouble();
                }
                return ret;
            }

            private int next() {
                if (numChars == -1)
                    throw new InputMismatchException();
                if (curChar >= numChars) {
                    curChar = 0;
                    try {
                        numChars = stream.read(buf);
                    } catch (IOException e) {
                        throw new InputMismatchException();
                    }
                    if (numChars <= 0)
                        return -1;
                }
                return buf[curChar++];
            }

            public char nextChar() {
                int c = next();
                while (isSpaceChar(c))
                    c = next();
                if ('a' <= c && c <= 'z') {
                    return (char) c;
                }
                if ('A' <= c && c <= 'Z') {
                    return (char) c;
                }
                throw new InputMismatchException();
            }

            public String nextToken() {
                int c = next();
                while (isSpaceChar(c))
                    c = next();
                StringBuilder res = new StringBuilder();
                do {
                    res.append((char) c);
                    c = next();
                } while (!isSpaceChar(c));
                return res.toString();
            }

            public int nextInt() {
                int c = next();
                while (isSpaceChar(c))
                    c = next();
                int sgn = 1;
                if (c == '-') {
                    sgn = -1;
                    c = next();
                }
                int res = 0;
                do {
                    if (c < '0' || c > '9')
                        throw new InputMismatchException();
                    res *= 10;
                    res += c - '0';
                    c = next();
                } while (!isSpaceChar(c));
                return res * sgn;
            }

            public long nextLong() {
                int c = next();
                while (isSpaceChar(c))
                    c = next();
                long sgn = 1;
                if (c == '-') {
                    sgn = -1;
                    c = next();
                }
                long res = 0;
                do {
                    if (c < '0' || c > '9')
                        throw new InputMismatchException();
                    res *= 10;
                    res += c - '0';
                    c = next();
                } while (!isSpaceChar(c));
                return res * sgn;
            }

            public double nextDouble() {
                return Double.valueOf(nextToken());
            }

            public boolean isSpaceChar(int c) {
                return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
            }

            public interface SpaceCharFilter {
                public boolean isSpaceChar(int ch);
            }
        }

        static void debug(Object... o) {
            System.err.println(Arrays.deepToString(o));
        }
    }
}
