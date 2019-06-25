package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

public class BZOJ1150 {
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

        int[] dists;

        public int distBetween(int i, int j) {
            return Math.abs(dists[j] - dists[i]);
        }

        public void solve() {
            int n = io.readInt();
            int k = io.readInt();
            dists = new int[n + 1];

            TreeSet<Integer> availableOddNum = new TreeSet();
            TreeSet<Integer> availableEvenNum = new TreeSet();
            int last = 0;
            for (int i = 1; i <= n; i++) {
                int next = io.readInt();
                int d = next - last;
                dists[i] = dists[i - 1];
                if (i % 2 == 1) {
                    availableOddNum.add(i);
                    dists[i] += d;
                } else {
                    availableEvenNum.add(i);
                    dists[i] -= d;
                }
                last = next;
            }

            TreeSet<Pair> pairs = new TreeSet(Pair.sortByDist);
            for (Integer odd : availableOddNum) {
                Integer floor = availableEvenNum.floor(odd);
                Integer ceil = availableEvenNum.ceiling(odd);

                if (floor != null) {
                    pairs.add(new Pair(odd, floor, distBetween(odd, floor)));
                }
                if (ceil != null) {
                    pairs.add(new Pair(odd, ceil, distBetween(odd, ceil)));
                }
            }

            //debug.assertTrue(distBetween(2, 3) == 1);
            int min = 0;
            while (k > 0) {
                Pair nearest = pairs.pollFirst();
                if (!(availableOddNum.contains(nearest.a)
                        && availableEvenNum.contains(nearest.b))) {
                    continue;
                }
                debug.debug("pair", nearest);
                min += nearest.dist;
                availableOddNum.remove(nearest.a);
                availableEvenNum.remove(nearest.b);
                Integer floorOdd = availableOddNum.floor(nearest.a);
                Integer ceilOdd = availableOddNum.ceiling(nearest.a);
                Integer floorEven = availableEvenNum.floor(nearest.a);
                Integer ceilEven = availableEvenNum.ceiling(nearest.a);
                if (floorOdd != null && ceilEven != null && availableEvenNum.ceiling(floorOdd) == ceilEven) {
                    pairs.add(new Pair(floorOdd, ceilEven, distBetween(floorOdd, ceilEven)));
                }
                if (floorEven != null && ceilOdd != null
                        && availableEvenNum.floor(ceilOdd) == floorEven) {
                    pairs.add(new Pair(ceilOdd, floorEven, distBetween(ceilOdd, floorEven)));
                }
                k--;
            }
            io.cache.append(min);
        }
    }


    public static class Pair {
        final int a;
        final int b;
        final int dist;

        public static Comparator<Pair> sortByDist = new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                int d = o1.dist - o2.dist;
                if (d == 0) {
                    d = o1.a - o2.a;
                }
                if (d == 0) {
                    d = o1.b - o2.b;
                }
                return d;
            }
        };

        @Override
        public String toString() {
            return String.format("a=%d,b=%d dist=%d", a, b, dist);
        }

        public Pair(int a, int b, int dist) {
            this.a = a;
            this.b = b;
            this.dist = dist;
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
