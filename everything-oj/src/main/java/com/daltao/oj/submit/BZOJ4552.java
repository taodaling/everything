package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.TreeMap;

public class BZOJ4552 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getSecurityManager() == null;
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


        TreeMap<Integer, Interval> map = new TreeMap();
        int n;

        public void solve() {
            n = io.readInt();
            int m = io.readInt();

            for (int i = 1; i <= n; i++) {
                MergeAbleSegment segment = MergeAbleSegment.alloc();
                segment.update(io.readInt(), 1, n, 1);
                map.put(i, new Interval(segment, i, i));
            }
            for (int i = 1; i <= m; i++) {
                int op = io.readInt();
                int l = io.readInt();
                int r = io.readInt();
                splitPoint(l);
                splitPoint(r + 1);

                Interval left = map.get(l);
                while (left.r < r) {
                    Interval next = map.remove(left.r + 1);
                    left.r = next.r;
                    left.segment.merge(1, n, next.segment);
                }

                left.rev = op == 1;
            }

            int q = io.readInt();
            Interval interval = map.floorEntry(q).getValue();
            int k = q - interval.l + 1;
            if (interval.rev) {
                k = interval.segment.cnt - k + 1;
            }

            io.cache.append(interval.segment.kth(1, n, k));
        }


        public void splitPoint(int x) {
            if (map.containsKey(x)) {
                return;
            }

            Interval floor = map.floorEntry(x).getValue();
            int k = x - floor.l;
            Interval right = floor.clone();
            floor.r = x - 1;
            right.l = x;
            if (!floor.rev) {
                floor.segment = floor.segment.splitByKth(k, 1, n);
            } else {
                right.segment = floor.segment.splitByKth(right.segment.cnt - k,
                        1, n);
            }

            map.put(x, right);
        }
    }

    public static class Interval implements Cloneable {
        MergeAbleSegment segment;
        int l;
        int r;
        boolean rev;

        public Interval(MergeAbleSegment segment, int l, int r) {
            this.segment = segment;
            this.l = l;
            this.r = r;
        }

        @Override
        public Interval clone() {
            try {
                return (Interval) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            return l + "," + r;
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

    public static class MergeAbleSegment implements Cloneable {
        private static final MergeAbleSegment NIL = new MergeAbleSegment();
        private static Deque<MergeAbleSegment> allocator = new ArrayDeque();

        public static MergeAbleSegment alloc() {
            return new MergeAbleSegment();
        }

        public static void destroy(MergeAbleSegment segment) {
            //allocator.addLast(segment);
        }

        static {
            NIL.left = NIL;
            NIL.right = NIL;
        }

        private MergeAbleSegment left;
        private MergeAbleSegment right;
        private int cnt;

        public void pushUp() {
            cnt = left.cnt + right.cnt;
        }

        public void pushDown() {
        }

        public MergeAbleSegment() {
            left = right = NIL;
        }

        private boolean covered(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        private boolean noIntersection(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public void update(int x, int l, int r, int mod) {
            if (l == r) {
                cnt += mod;
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            if (x <= m) {
                if (left == NIL) {
                    left = alloc();
                }
                left.update(x, l, m, mod);
            } else {
                if (right == NIL) {
                    right = alloc();
                }
                right.update(x, m + 1, r, mod);
            }
            pushUp();
        }

        public int kth(int l, int r, int k) {
            if (l == r) {
                return l;
            }
            int m = (l + r) >> 1;
            if (left.cnt >= k) {
                return left.kth(l, m, k);
            } else {
                return right.kth(m + 1, r, k - left.cnt);
            }
        }

        public int query(int ll, int rr, int l, int r) {
            if (noIntersection(ll, rr, l, r)) {
                return 0;
            }
            if (covered(ll, rr, l, r)) {
                return cnt;
            }
            int m = (l + r) >> 1;
            return left.query(ll, rr, l, m) +
                    right.query(ll, rr, m + 1, r);
        }

        /**
         * split this by kth element, and kth element belong to the left part.
         * Return the k-th element as result
         */
        public MergeAbleSegment splitByKth(int k, int l, int r) {
            MergeAbleSegment ret = alloc();
            if (l == r) {
                ret.cnt = k;
                cnt -= k;
                return ret;
            }
            int m = (l + r) >> 1;
            if (k >= left.cnt) {
                k -= left.cnt;
                ret.left = left;
                left = NIL;
            } else {
                ret.left = left.splitByKth(k, l, m);
                k = 0;
            }
            if (k > 0) {
                if (k >= right.cnt) {
                    ret.right = right;
                    right = NIL;
                } else {
                    ret.right = right.splitByKth(k, l, m);
                }
            }

            ret.pushUp();
            this.pushUp();
            return ret;
        }

        public MergeAbleSegment merge(int l, int r, MergeAbleSegment segment) {
            if (this == NIL) {
                return segment;
            } else if (segment == NIL) {
                return this;
            }
            if (l == r) {
                cnt += segment.cnt;
                destroy(segment);
                return this;
            }
            int m = (l + r) >> 1;
            left = left.merge(l, m, segment.left);
            right = right.merge(m + 1, r, segment.right);
            destroy(segment);
            pushUp();
            return this;
        }
    }

}
