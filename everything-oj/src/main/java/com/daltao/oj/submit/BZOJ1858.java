package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class BZOJ1858 {
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

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();
            int[] data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = io.readInt();
            }
            Segment root = Segment.build(0, n - 1, data);
            for (int i = 0; i < m; i++) {
                int c = io.readInt();
                int l = io.readInt();
                int r = io.readInt();
                if (c == 0) {
                    Segment.update(l, r, 0, n - 1, root, new Segment.Consumer<Segment>() {
                        @Override
                        public void consume(Segment val) {
                            val.set0();
                        }
                    });
                } else if (c == 1) {
                    Segment.update(l, r, 0, n - 1, root, new Segment.Consumer<Segment>() {
                        @Override
                        public void consume(Segment val) {
                            val.set1();
                        }
                    });
                } else if (c == 2) {
                    Segment.update(l, r, 0, n - 1, root, new Segment.Consumer<Segment>() {
                        @Override
                        public void consume(Segment val) {
                            val.rev();
                        }
                    });
                } else if (c == 2) {
                    Segment.update(l, r, 0, n - 1, root, new Segment.Consumer<Segment>() {
                        @Override
                        public void consume(Segment val) {
                            val.rev();
                        }
                    });
                } else if (c == 3) {
                    io.cache.append(Segment.query(l, r, 0, n - 1, root)).append('\n');
                } else {
                    io.cache.append(Segment.queryLongestOneSeq(l, r, 0, n - 1, root)[1]).append('\n');
                }
            }
        }
    }


    private static class Segment implements Cloneable {
        Segment left;
        Segment right;
        int val;

        boolean set1;
        boolean set0;
        boolean rev;

        public static final int[] DEF_VAL = new int[3];

        int[][] longest = new int[2][3];

        int size = 1;
        int[] cnt = new int[2];

        public static interface Consumer<T> {
            public void consume(T val);
        }


        public void set1() {
            this.set1 = true;
            this.set0 = false;
            this.rev = false;
            Arrays.fill(longest[1], size);
            Arrays.fill(longest[0], 0);
            val = 1;
            cnt[1] = size;
            cnt[0] = 0;
        }

        public void set0() {
            this.set0 = true;
            this.set1 = false;
            this.rev = false;
            Arrays.fill(longest[1], 0);
            Arrays.fill(longest[0], size);
            val = 0;
            cnt[1] = 0;
            cnt[0] = size;
        }

        public void rev() {
            if (set0 == true) {
                set1();
                return;
            } else if (set1 == true) {
                set0();
                return;
            }
            rev = !rev;
            Memory.swap(longest, 0, 1);
            Memory.swap(cnt, 0, 1);
            if (val == 0) {
                val = 1;
            } else {
                val = 0;
            }
        }

        public static Segment build(int l, int r, int[] data) {
            Segment segment = new Segment();
            if (l != r) {
                int m = (l + r) >> 1;
                segment.left = build(l, m, data);
                segment.right = build(m + 1, r, data);
                segment.pushUp();
            } else {
                if (data[l] == 0) {
                    segment.set0();
                } else {
                    segment.set1();
                }
            }
            return segment;
        }

        public static boolean checkOutOfRange(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public static boolean checkCoverage(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        public static void update(int ll, int rr, int l, int r, Segment segment, Consumer<Segment> consumer) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return;
            }

            if (checkCoverage(ll, rr, l, r)) {
                consumer.consume(segment);
                return;
            }
            int m = (l + r) >> 1;
            segment.pushDown();
            update(ll, rr, l, m, segment.left, consumer);
            update(ll, rr, m + 1, r, segment.right, consumer);
            segment.pushUp();
        }

        public static int query(int ll, int rr, int l, int r, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return 0;
            }
            if (checkCoverage(ll, rr, l, r)) {
                return segment.cnt[1];
            }
            segment.pushDown();
            int m = (l + r) >> 1;
            return query(ll, rr, l, m, segment.left) +
                    query(ll, rr, m + 1, r, segment.right);
        }

        public static int[] queryLongestOneSeq(int ll, int rr, int l, int r, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return DEF_VAL;
            }
            if (checkCoverage(ll, rr, l, r)) {
                return segment.longest[1];
            }
            int m = (l + r) >> 1;

            segment.pushDown();
            int[] result = new int[3];
            merge(queryLongestOneSeq(ll, rr, l, m, segment.left),
                    queryLongestOneSeq(ll, rr, m + 1, r, segment.right), segment.left.size, segment.right.size, result);
            return result;
        }

        public void pushDown() {
            if (set0) {
                left.set0();
                right.set0();
                set0 = false;
            } else if (set1) {
                left.set1();
                right.set1();
                set1 = false;
            } else if (rev) {
                left.rev();
                right.rev();
                rev = false;
            }
        }

        public static void merge(int[] left, int[] right, int leftSize, int rightSize, int[] current) {
            current[0] = left[0];
            if (left[0] == leftSize) {
                current[0] += right[0];
            }
            current[1] = Math.max(left[1], right[1]);
            current[1] = Math.max(current[1], left[2] + right[0]);
            current[2] = right[2];
            if (right[2] == rightSize) {
                current[2] += left[2];
            }
        }

        public void pushUp() {
            size = left.size + right.size;
            merge(left.longest[0], right.longest[0], left.size, right.size, longest[0]);
            merge(left.longest[1], right.longest[1], left.size, right.size, longest[1]);
            cnt[0] = left.cnt[0] + right.cnt[0];
            cnt[1] = left.cnt[1] + right.cnt[1];
        }

        @Override
        public Segment clone() {
            try {
                return (Segment) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public static Segment cloneTree(Segment node) {
            if (node == null) {
                return null;
            }
            node = node.clone();
            node.left = cloneTree(node.left);
            node.right = cloneTree(node.right);
            return node;
        }

        public static void toString(Segment node, StringBuilder builder) {
            if (node == null) {
                return;
            }
            if (node.left == null && node.right == null) {
                builder.append(node.val).append(',');
                return;
            }
            node.pushDown();
            toString(node.left, builder);
            toString(node.right, builder);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            toString(cloneTree(this), builder);
            if (builder.length() > 0) {
                builder.setLength(builder.length() - 1);
            }
            return builder.toString();
        }
    }


    public static class FastIO {
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        public final StringBuilder cache = new StringBuilder();

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
            long num = readLong();
            if (next != '.') {
                return num;
            }

            next = read();
            double f = readLong();
            while (f >= 100000000) {
                f /= 1000000000;
            }
            while (f >= 10000) {
                f /= 100000;
            }
            while (f >= 100) {
                f /= 1000;
            }
            while (f >= 1) {
                f /= 10;
            }
            return num > 0 ? (num + f) : (num - f);
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

    public static class Memory {
        public static <T> void swap(T[] data, int i, int j) {
            T tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static void swap(char[] data, int i, int j) {
            char tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static void swap(int[] data, int i, int j) {
            int tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static void swap(long[] data, int i, int j) {
            long tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static <T> int min(T[] data, int from, int to, Comparator<T> cmp) {
            int m = from;
            for (int i = from + 1; i < to; i++) {
                if (cmp.compare(data[m], data[i]) > 0) {
                    m = i;
                }
            }
            return m;
        }

        public static <T> void move(T[] data, int from, int to, int step) {
            int len = to - from;
            step = len - (step % len + len) % len;
            Object[] buf = new Object[len];
            for (int i = 0; i < len; i++) {
                buf[i] = data[(i + step) % len + from];
            }
            System.arraycopy(buf, 0, data, from, len);
        }

        public static <T> void reverse(T[] data, int f, int t) {
            int l = f, r = t - 1;
            while (l < r) {
                swap(data, l, r);
                l++;
                r--;
            }
        }

        public static void reverse(int[] data, int f, int t) {
            int l = f, r = t - 1;
            while (l < r) {
                swap(data, l, r);
                l++;
                r--;
            }
        }

        public static void copy(Object[] src, Object[] dst, int srcf, int dstf, int len) {
            if (len < 8) {
                for (int i = 0; i < len; i++) {
                    dst[dstf + i] = src[srcf + i];
                }
            } else {
                System.arraycopy(src, srcf, dst, dstf, len);
            }
        }
    }

    public static class Debug {
        private boolean allowDebug;

        public Debug(boolean allowDebug) {
            this.allowDebug = allowDebug;
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
