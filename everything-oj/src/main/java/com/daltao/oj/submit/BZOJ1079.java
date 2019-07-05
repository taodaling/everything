package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BZOJ1079 {
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
        Modular modular = new Modular((int) 1e9 + 7);

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }


        int[] f;
        ArrayIndex arrayIndex;

        public void solve() {
            int k = io.readInt();
            int[] cnts = new int[6];
            for (int i = 0; i < k; i++) {
                int c = io.readInt();
                cnts[c]++;
            }

            int size = (k + 1) * (k + 1) * (k + 1) * (k + 1) * (k + 1) * 5;
            arrayIndex = new ArrayIndex(k + 1, k + 1, k + 1, k + 1, k + 1, 5);
            f = new int[size];
            Arrays.fill(f, -1);
            f[arrayIndex.indexOf(1, 0, 0, 0, 0, 0)] = 1;

            int total = 0;
            total = modular.plus(total, modular.mul(f(cnts[1], cnts[2], cnts[3], cnts[4], cnts[5], 0), cnts[1]));
            total = modular.plus(total, modular.mul(f(cnts[1], cnts[2], cnts[3], cnts[4], cnts[5], 1), cnts[2]));
            total = modular.plus(total, modular.mul(f(cnts[1], cnts[2], cnts[3], cnts[4], cnts[5], 2), cnts[3]));
            total = modular.plus(total, modular.mul(f(cnts[1], cnts[2], cnts[3], cnts[4], cnts[5], 3), cnts[4]));
            total = modular.plus(total, modular.mul(f(cnts[1], cnts[2], cnts[3], cnts[4], cnts[5], 4), cnts[5]));
            io.cache.append(total);
        }

        public int f(int i0, int i1, int i2, int i3, int i4, int first) {
            if (i0 < 0 || i1 < 0 || i2 < 0 || i3 < 0 || i4 < 0) {
                return 0;
            }
            int index = arrayIndex.indexOf(i0, i1, i2, i3, i4, first);
            if (f[index] == -1) {
                f[index] = 0;
                switch (first) {
                    case 0:
                        i0--;
                        break;
                    case 1:
                        i1--;
                        i0++;
                        break;
                    case 2:
                        i2--;
                        i1++;
                        break;
                    case 3:
                        i3--;
                        i2++;
                        break;
                    case 4:
                        i4--;
                        i3++;
                        break;
                }
                for (int l = 0; l <= 4; l++) {
                    int cnt = 0;
                    switch (l) {
                        case 0:
                            cnt = i0;
                            break;
                        case 1:
                            cnt = i1;
                            break;
                        case 2:
                            cnt = i2;
                            break;
                        case 3:
                            cnt = i3;
                            break;
                        case 4:
                            cnt = i4;
                            break;
                    }
                    if (l + 1 == first) {
                        cnt--;
                    }
                    f[index] = modular.plus(f[index], modular.mul(f(i0, i1, i2, i3, i4, l), cnt));
                }
            }

            return f[index];
        }
    }

    /**
     * 模运算
     */
    public static class Modular {
        final int m;

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

        public int indexOf(int a, int b, int c, int d, int e, int f) {
            return indexOf(a, b, c, d, e) * dimensions[5] + f;
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
