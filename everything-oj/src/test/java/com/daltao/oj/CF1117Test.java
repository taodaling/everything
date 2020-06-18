package com.daltao.oj;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.io.IOException;
import java.util.Deque;
import java.util.function.Supplier;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import java.io.Closeable;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.util.ArrayDeque;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Closeable;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.InputStream;


public class CF1117Test {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(C.class)))
        .setInputFactory(new Generator())
        .setTestTime(10000)
        .build().call());
    }

    /**
     * Built using CHelper plug-in
     * Actual solution is at the top
     */
    public static class Main {
        public static void main(String[] args) throws Exception {
            Thread thread = new Thread(null, new TaskAdapter(), "", 1 << 27);
            thread.start();
            thread.join();
        }

        static class TaskAdapter implements Runnable {
            @Override
            public void run() {
                InputStream inputStream = System.in;
                OutputStream outputStream = System.out;
                FastInput in = new FastInput(inputStream);
                FastOutput out = new FastOutput(outputStream);
                TaskC solver = new TaskC();
                solver.solve(1, in, out);
                out.close();
            }
        }

        static class TaskC {
            public void solve(int testNumber, FastInput in, FastOutput out) {
                long[] src = new long[]{in.readInt(), in.readInt()};
                long[] dst = new long[]{in.readInt(), in.readInt()};
                int n = in.readInt();
                int[][] dxy = new int[n][2];
                for (int i = 0; i < n; i++) {
                    switch (in.readChar()) {
                        case 'U':
                            dxy[i][1] = 1;
                            break;
                        case 'D':
                            dxy[i][1] = -1;
                            break;
                        case 'L':
                            dxy[i][0] = -1;
                            break;
                        case 'R':
                            dxy[i][0] = 1;
                            break;
                    }
                }
                long[] trace = src.clone();
                long[] sum = new long[2];
                for (int i = 0; i < n; i++) {
                    trace[0] += dxy[i][0];
                    trace[1] += dxy[i][1];
                    sum[0] += dxy[i][0];
                    sum[1] += dxy[i][1];
                }

                long shrink = dist(src, dst) - (dist(trace, dst) - n);
                if (shrink == 0) {
                    out.println(-1);
                    return;
                }
                long loopNeed = dist(src, dst) / shrink;
                long time = loopNeed * n;
                trace[0] = src[0] + sum[0] * loopNeed;
                trace[1] = src[1] + sum[1] * loopNeed;
                for (int i = 0; i < n && dist(trace, dst) > time; i++) {
                    trace[0] += dxy[i][0];
                    trace[1] += dxy[i][1];
                    time++;
                }

                out.println(time);
            }

            public long dist(long[] a, long[] b) {
                return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
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
                        bufLen = -1;
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

            public char readChar() {
                skipBlank();
                char c = (char) next;
                next = read();
                return c;
            }

        }

        static class FastOutput implements AutoCloseable, Closeable {
            private StringBuilder cache = new StringBuilder(10 << 20);
            private final Writer os;

            public FastOutput(Writer os) {
                this.os = os;
            }

            public FastOutput(OutputStream os) {
                this(new OutputStreamWriter(os));
            }

            public FastOutput println(int c) {
                cache.append(c).append('\n');
                return this;
            }

            public FastOutput println(long c) {
                cache.append(c).append('\n');
                return this;
            }

            public FastOutput flush() {
                try {
                    os.append(cache);
                    os.flush();
                    cache.setLength(0);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return this;
            }

            public void close() {
                flush();
                try {
                    os.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public String toString() {
                return cache.toString();
            }

        }
    }



    //package educational.round60;
    public static class C {
        InputStream is;
        PrintWriter out;
        String INPUT = "";

        void solve() {
            long x1 = ni(), y1 = ni();
            long x2 = ni(), y2 = ni();
            int n = ni();
            char[] s = ns(n);
            x2 -= x1;
            y2 -= y1;

            long low = -1, high = (long) (2e16);
            long q = high;
            while (high - low > 1) {
                long h = high + low >> 1;
                if (ok(h, x2, y2, s)) {
                    high = h;
                } else {
                    low = h;
                }
            }
            if (high >= q / 2) {
                out.println(-1);
            } else {
                out.println(high);
            }
        }

        boolean ok(long h, long x, long y, char[] s) {
            int[] dx = {1, 0, -1, 0};
            int[] dy = {0, 1, 0, -1};
            String D = "RULD";

            long n = s.length;
            for (int i = 0; i < s.length; i++) {
                int ind = D.indexOf(s[i]);
                x -= (h + n - 1 - i) / n * dx[ind];
                y -= (h + n - 1 - i) / n * dy[ind];
            }
            return Math.abs(x) + Math.abs(y) <= h;
        }

        void run() throws Exception {
            is = oj ? System.in : new ByteArrayInputStream(INPUT.getBytes());
            out = new PrintWriter(System.out);

            long s = System.currentTimeMillis();
            solve();
            out.flush();
            tr(System.currentTimeMillis() - s + "ms");
        }

        public static void main(String[] args) throws Exception {
            new C().run();
        }

        private byte[] inbuf = new byte[1024];
        public int lenbuf = 0, ptrbuf = 0;

        private int readByte() {
            if (lenbuf == -1) throw new InputMismatchException();
            if (ptrbuf >= lenbuf) {
                ptrbuf = 0;
                try {
                    lenbuf = is.read(inbuf);
                } catch (IOException e) {
                    throw new InputMismatchException();
                }
                if (lenbuf <= 0) return -1;
            }
            return inbuf[ptrbuf++];
        }

        private boolean isSpaceChar(int c) {
            return !(c >= 33 && c <= 126);
        }

        private int skip() {
            int b;
            while ((b = readByte()) != -1 && isSpaceChar(b)) ;
            return b;
        }

        private double nd() {
            return Double.parseDouble(ns());
        }

        private char nc() {
            return (char) skip();
        }

        private String ns() {
            int b = skip();
            StringBuilder sb = new StringBuilder();
            while (!(isSpaceChar(b))) { // when nextLine, (isSpaceChar(b) && b != ' ')
                sb.appendCodePoint(b);
                b = readByte();
            }
            return sb.toString();
        }

        private char[] ns(int n) {
            char[] buf = new char[n];
            int b = skip(), p = 0;
            while (p < n && !(isSpaceChar(b))) {
                buf[p++] = (char) b;
                b = readByte();
            }
            return n == p ? buf : Arrays.copyOf(buf, p);
        }

        private char[][] nm(int n, int m) {
            char[][] map = new char[n][];
            for (int i = 0; i < n; i++) map[i] = ns(m);
            return map;
        }

        private int[] na(int n) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = ni();
            return a;
        }

        private int ni() {
            int num = 0, b;
            boolean minus = false;
            while ((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-')) ;
            if (b == '-') {
                minus = true;
                b = readByte();
            }

            while (true) {
                if (b >= '0' && b <= '9') {
                    num = num * 10 + (b - '0');
                } else {
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private long nl() {
            long num = 0;
            int b;
            boolean minus = false;
            while ((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-')) ;
            if (b == '-') {
                minus = true;
                b = readByte();
            }

            while (true) {
                if (b >= '0' && b <= '9') {
                    num = num * 10 + (b - '0');
                } else {
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private boolean oj = System.getProperty("ONLINE_JUDGE") != null;

        private void tr(Object... o) {
            if (!oj) System.out.println(Arrays.deepToString(o));
        }
    }

    static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput in = new QueueInput();
            in.add(nextInt(0, 10));
            in.add(nextInt(0, 10));
            in.add(nextInt(0, 10));
            in.add(nextInt(0, 10));

            char[] cmd = "UDLR".toCharArray();
            int n = nextInt(1, 3);
            StringBuilder builder = new StringBuilder();
            in.add(n);
            for (int i = 0; i < n; i++) {
                builder.append(cmd[nextInt(0, cmd.length - 1)]);
            }
            in.add(builder.toString());
            return in.end();
        }
    }
}
