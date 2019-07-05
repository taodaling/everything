package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;


public class CFContest {
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
        int inf = (int) 1e9 + 2;
        BitOperator bitOperator = new BitOperator();
        Modular modular = new Modular((int) 1e9 + 7);

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        int n;
        int t;
        int[] songTimes;
        int[] songTypes;
        int mask;
        int[] cnts = new int[4];
        int[][][][] perm;
        int ct1 ;
        int ct2 ;
        int ct3 ;
        public void solve() {
            n = io.readInt();
            t = io.readInt();


            songTimes = new int[n + 1];
            songTypes = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                songTimes[i] = io.readInt();
                songTypes[i] = io.readInt();
                cnts[songTypes[i]]++;
            }

            ct1 = cnts[1];
            ct2 = cnts[2];
            ct3 = cnts[3];
            ArrayIndex arrayIndex = new ArrayIndex(ct1 + 1, ct2 + 1, ct3 + 1, t + 1);

            int[] fr = new int[(ct1 + 1) * (ct2 + 1) * (ct3 + 1) * (t + 1)];
            int[] fw = new int[fr.length];
            fr[0] = 1;
            int[] abc = new int[4];
            for (int i = 1; i <= n; i++) {
                for (abc[1] = 0; abc[1] <= ct1; abc[1]++) {
                    for (abc[2] = 0; abc[2] <= ct2; abc[2]++) {
                        for (abc[3] = 0; abc[3] <= ct3; abc[3]++) {
                            int a = abc[1];
                            int b = abc[2];
                            int c = abc[3];
                            for (int d = 0; d <= t; d++) {
                                int index = arrayIndex.indexOf(a, b, c, d);
                                fw[index] = fr[index];
                                if (abc[songTypes[i]] > 0 && d >= songTimes[i]) {
                                    abc[songTypes[i]]--;
                                    fw[index] = modular.plus(fw[index],
                                            fr[arrayIndex.indexOf(abc[1], abc[2], abc[3], d - songTimes[i])]);
                                    abc[songTypes[i]]++;
                                }
                            }
                        }
                    }
                }
                int[] tmp = fr;
                fr = fw;
                fw = tmp;
            }

            perm = new int[ct1 + 1][ct2 + 1][ct3 + 1][4];
            for (int a = 0; a <= ct1; a++) {
                for (int b = 0; b <= ct2; b++) {
                    for (int c = 0; c <= ct3; c++) {
                        for (int d = 0; d < 4; d++) {
                            perm[a][b][c][d] = -1;
                        }
                    }
                }
            }
            perm[0][0][0][0] = 1;

            int ans = 0;
            for (int a = 0; a <= ct1; a++) {
                for (int b = 0; b <= ct2; b++) {
                    for (int c = 0; c <= ct3; c++) {
                        int index = arrayIndex.indexOf(a, b, c, t);
                        for (int d = 0; d < 4; d++) {
                            int p = modular.mul(fr[index], perm(a, b, c, d));
                            ans = modular.plus(ans, p);
                        }
                    }
                }
            }

            io.cache.append(ans);
        }

        int perm(int a, int b, int c, int d) {
            if (a < 0 || b < 0 || c < 0) {
                return 0;
            }
            if (perm[a][b][c][d] == -1) {
                perm[a][b][c][d] = 0;
                int aa = a;
                int bb = b;
                int cc = c;
                if (d == 0) {
                    return perm[a][b][c][d];
                }
                int mul = 1;
                if (d == 1) {
                    mul = a;
                    aa--;
                } else if (d == 2) {
                    mul = b;
                    bb--;
                } else if (d == 3) {
                    mul = c;
                    cc--;
                }

                for (int k = 0; k < 4; k++) {
                    if (k == d) {
                        continue;
                    }
                    perm[a][b][c][d] = modular.plus(perm[a][b][c][d],
                            perm(aa, bb, cc, k));
                }
                perm[a][b][c][d] = modular.mul(perm[a][b][c][d], mul);
            }

            return perm[a][b][c][d];
        }
    }

    public static class ArrayIndex {
        int[] dimensions;

        public ArrayIndex(int... dimensions) {
            this.dimensions = dimensions;
        }

        public int indexOf(int a, int b) {
            return a * dimensions[1] + b;
        }

        public int indexOf(int a, int b, int c) {
            return indexOf(a, b) * dimensions[2] + c;
        }

        public int indexOf(int a, int b, int c, int d) {
            return indexOf(a, b, c) * dimensions[3] + d;
        }

        public int indexOf(int a, int b, int c, int d, int e) {
            return indexOf(a, b, c, d) * dimensions[4] + e;
        }

        public boolean isValid(int a, int d) {
            return dimensions[d] > a && a >= 0;
        }

        public boolean isValidIndex(int a) {
            return isValid(a, 0);
        }

        public boolean isValidIndex(int a, int b) {
            return isValidIndex(a) && isValid(b, 1);
        }

        public boolean isValidIndex(int a, int b, int c) {
            return isValidIndex(a, b) && isValid(c, 2);
        }

        public boolean isValidIndex(int a, int b, int c, int d) {
            return isValidIndex(a, b, c) && isValid(d, 3);
        }

        public int indexOfSpecifiedDimension(int index, int d) {
            return indexOfSpecifiedDimension0(index, d, dimensions.length - 1);
        }

        private int indexOfSpecifiedDimension0(int index, int t, int now) {
            return now == t ? index % dimensions[now] : indexOfSpecifiedDimension0(index / dimensions[now], t, now - 1);
        }
    }

    /**
     * 模运算
     */
    public static class Modular {
        int m;

        public Modular(int m) {
            this.m = m;
        }

        public int valueOf(int x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return x;
        }

        public int valueOf(long x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return (int) x;
        }

        public int mul(int x, int y) {
            return valueOf((long) x * y);
        }

        public int plus(int x, int y) {
            return valueOf(x + y);
        }

        @Override
        public String toString() {
            return "mod " + m;
        }
    }

    public static class BitOperator {
        public int bitAt(int x, int i) {
            return (x >> i) & 1;
        }

        public int bitAt(long x, int i) {
            return (int) ((x >> i) & 1);
        }

        public int setBit(int x, int i, boolean v) {
            if (v) {
                x |= 1 << i;
            } else {
                x &= ~(1 << i);
            }
            return x;
        }

        public long setBit(long x, int i, boolean v) {
            if (v) {
                x |= 1L << i;
            } else {
                x &= ~(1L << i);
            }
            return x;
        }

        /**
         * Determine whether x is subset of y
         */
        public boolean subset(long x, long y) {
            return intersect(x, y) == x;
        }

        /**
         * Merge two set
         */
        public long merge(long x, long y) {
            return x | y;
        }

        public long intersect(long x, long y) {
            return x & y;
        }

        public long differ(long x, long y) {
            return x - intersect(x, y);
        }
    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder();
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
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

        public void flush() {
            try {
                os.write(cache.toString().getBytes(charset));
                os.flush();
                cache.setLength(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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