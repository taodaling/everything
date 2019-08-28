package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class LUOGU3772 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("D:\\DATABASE\\TESTCASE\\Code.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io, new Debug(local));

        if (async) {
            Thread t = new Thread(null, task, "dalt", 1 << 27);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            t.join();
        } else {
            task.run();
        }

        if (local) {
            io.cache.append("\n\n--memory -- \n" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + "M");
        }

        io.flush();
    }

    public static class Task implements Runnable {
        final FastIO io;
        final Debug debug;
        int inf = (int) 1e8;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        char[] ignore = new char[1000];

        public void ignore() {
            io.readString(ignore, 0);
        }

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();
            ignore();

            double[][] pq = new double[n + 1][2];
            pq[1][0] = io.readDouble();
            for (int i = 2; i <= n; i++) {
                pq[i][0] = io.readDouble();
                pq[i][1] = io.readDouble();
            }

            Segment segment = new Segment(1, n, pq);

            for (int i = 0; i < m; i++) {
                io.readString(ignore, 0);
                if (ignore[0] == 'a') {
                    int j = io.readInt();
                    int c = io.readInt();
                    if (c == 1) {
                        segment.updateWin(j, j, 1, n);
                    } else {
                        segment.updateLose(j, j, 1, n);
                    }
                } else {
                    int j = io.readInt();
                    segment.updateUnknown(j, j, 1, n, pq[j][0], pq[j][1]);
                }
                io.cache.append(segment.expectation[1]).append('\n');
            }
        }
    }

    public static class Segment implements Cloneable {
        private Segment left;
        private Segment right;
        private double[][] prob = new double[2][2];
        private double[] expectation = new double[2];

        public void pushUp() {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    prob[i][j] = 0;
                    for (int k = 0; k < 2; k++) {
                        prob[i][j] += left.prob[i][k] * right.prob[k][j];
                    }
                }
            }

            for (int i = 0; i < 2; i++) {
                expectation[i] = left.expectation[i];
                for (int j = 0; j < 2; j++) {
                    expectation[i] += right.expectation[j] * left.prob[i][j];
                }
            }
        }

        private void updateExpect() {
            expectation[0] = prob[0][1];
            expectation[1] = prob[1][1];
        }

        public void setUnknow(double p, double q) {
            prob[1][1] = p;
            prob[1][0] = 1 - p;
            prob[0][1] = q;
            prob[0][0] = 1 - q;
            updateExpect();
        }

        public void setWin() {
            prob[0][1] = 1;
            prob[0][0] = 0;
            prob[1][0] = 0;
            prob[1][1] = 1;
            updateExpect();
        }

        public void setLose() {
            prob[0][1] = 0;
            prob[0][0] = 1;
            prob[1][0] = 1;
            prob[1][1] = 0;
            updateExpect();
        }

        public void pushDown() {
        }

        public Segment(int l, int r, double[][] pq) {
            if (l < r) {
                int m = (l + r) >> 1;
                left = new Segment(l, m, pq);
                right = new Segment(m + 1, r, pq);
                pushUp();
            } else {
                setUnknow(pq[l][0], pq[l][1]);
            }
        }

        private boolean covered(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        private boolean noIntersection(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public void updateWin(int ll, int rr, int l, int r) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                setWin();
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.updateWin(ll, rr, l, m);
            right.updateWin(ll, rr, m + 1, r);
            pushUp();
        }

        public void updateLose(int ll, int rr, int l, int r) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                setLose();
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.updateLose(ll, rr, l, m);
            right.updateLose(ll, rr, m + 1, r);
            pushUp();
        }

        public void updateUnknown(int ll, int rr, int l, int r, double p, double q) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                setUnknow(p, q);
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.updateUnknown(ll, rr, l, m, p, q);
            right.updateUnknown(ll, rr, m + 1, r, p, q);
            pushUp();
        }

        public void query(int ll, int rr, int l, int r) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.query(ll, rr, l, m);
            right.query(ll, rr, m + 1, r);
        }
    }


    public static class FastIO {
        public final StringBuilder cache = new StringBuilder(1 << 13);
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 13);
        private byte[] buf = new byte[1 << 13];
        private int bufLen;
        private int bufOffset;
        private int next;

        public FastIO(InputStream is, OutputStream os, Charset charset) {
            this.is = is;
            this.os = os;
            this.charset = charset;
        }

        public FastIO(InputStream is, OutputStream os) {
            this(is, os, Charset.forName("ascii"));
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

        public long readLong() {
            int sign = 1;

            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+' ? 1 : -1;
                next = read();
            }

            long val = 0;
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

        public double readDouble() {
            boolean sign = true;
            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+';
                next = read();
            }

            long val = 0;
            while (next >= '0' && next <= '9') {
                val = val * 10 + next - '0';
                next = read();
            }
            if (next != '.') {
                return sign ? val : -val;
            }
            next = read();
            long radix = 1;
            long point = 0;
            while (next >= '0' && next <= '9') {
                point = point * 10 + next - '0';
                radix = radix * 10;
                next = read();
            }
            double result = val + (double) point / radix;
            return sign ? result : -result;
        }

        public String readString(StringBuilder builder) {
            skipBlank();

            while (next > 32) {
                builder.append((char) next);
                next = read();
            }

            return builder.toString();
        }

        public String readString() {
            defaultStringBuf.setLength(0);
            return readString(defaultStringBuf);
        }

        public int readLine(char[] data, int offset) {
            int originalOffset = offset;
            while (next != -1 && next != '\n') {
                data[offset++] = (char) next;
                next = read();
            }
            return offset - originalOffset;
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

        public int readString(byte[] data, int offset) {
            skipBlank();

            int originalOffset = offset;
            while (next > 32) {
                data[offset++] = (byte) next;
                next = read();
            }

            return offset - originalOffset;
        }

        public char readChar() {
            skipBlank();
            char c = (char) next;
            next = read();
            return c;
        }

        public void flush() throws IOException {
            os.write(cache.toString().getBytes(charset));
            os.flush();
            cache.setLength(0);
        }

        public boolean hasMore() {
            skipBlank();
            return next != -1;
        }
    }

    public static class Debug {
        private boolean allowDebug;

        public Debug(boolean allowDebug) {
            this.allowDebug = allowDebug;
        }

        public void assertTrue(boolean flag) {
            if (!allowDebug) {
                return;
            }
            if (!flag) {
                fail();
            }
        }

        public void fail() {
            throw new RuntimeException();
        }

        public void assertFalse(boolean flag) {
            if (!allowDebug) {
                return;
            }
            if (flag) {
                fail();
            }
        }

        private void outputName(String name) {
            System.out.print(name + " = ");
        }

        public void debug(String name, int x) {
            if (!allowDebug) {
                return;
            }

            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, long x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, double x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, int[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, long[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, double[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, Object x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, Object... x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.deepToString(x));
        }
    }
}
