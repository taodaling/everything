package com.daltao.oj.submit;

import com.daltao.template.Randomized;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class BZOJ2648 {
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
        }
    }

    public static class Rect {
        int xMin;
        int xMax;
        int yMin;
        int yMax;

        public void maxRectContain(Rect left, Rect right, int[] position) {
            xMax = Math.max(left.xMax, right.xMax);
            xMax = Math.max(xMax, position[0]);

            xMin = Math.min(left.xMin, right.xMin);
            xMin = Math.min(xMin, position[0]);

            yMax = Math.max(left.yMax, right.yMax);
            yMax = Math.max(yMax, position[1]);

            yMin = Math.min(left.yMin, right.yMin);
            yMin = Math.min(yMin, position[1]);
        }

        public static boolean intersect(Rect a, Rect b) {
            return a.xMin < b.xMax && b.xMin < a.xMax &&
                    a.yMin < b.yMax && b.yMin < a.yMax;
        }
    }

    public static class KDNode {
        public static final KDNode[] RECORD = new KDNode[1000000];
        public static final Comparator<KDNode>[] COMPARATORS = new Comparator[2];
        public static final KDNode NIL = new KDNode();
        public static final double FACTOR = 0.75;

        public static final int INF = (int) 1e8;

        static {
            NIL.position = null;
            NIL.left = NIL.right = NIL;
            NIL.size = 0;
            NIL.rect.xMax = (int) -INF;
            NIL.rect.xMin = (int) INF;
            NIL.rect.yMax = (int) -INF;
            NIL.rect.yMin = (int) INF;
        }

        static {
            for (int i = 0; i < 2; i++) {
                final int j = i;
                COMPARATORS[i] = new Comparator<KDNode>() {
                    @Override
                    public int compare(KDNode o1, KDNode o2) {
                        return o1.position[j] - o2.position[j];
                    }
                };
            }
        }

        public static int RECORD_LENGTH = 0;

        KDNode left = NIL;
        KDNode right = NIL;
        int[] position = new int[2];
        int size = 1;
        Rect rect = new Rect();

        public void pushUp() {
            size = left.size + right.size + 1;
            rect.maxRectContain(left.rect, right.rect, position);
        }

        public void pushDown() {
        }

        public void query(int[] pos, Rect range, int depth) {
            if (!Rect.intersect(rect, range)) {
                return;
            }
            pushDown();
            left = check(left, depth + 1);
            right = check(right, depth + 1);
            int distance = Math.abs(position[0] - pos[0]) + Math.abs(position[1] - pos[1]);
            if (distance < pos[0] - range.xMin) {
                range.yMin = pos[1] - distance;
                range.yMax = pos[1] + distance;
                range.xMin = pos[0] - distance;
                range.xMax = pos[0] + distance;
            }

            if(left.)
            left.query(pos, range, depth + 1);
            right.query(pos, range, depth + 1);
        }

        public KDNode insert(KDNode node, int depth) {
            if (this == NIL) {
                return node;
            }
            pushDown();
            left = check(left, depth + 1);
            right = check(right, depth + 1);

            if (COMPARATORS[depth & 1].compare(this, node) >= 0) {
                left = left.insert(node, depth + 1);
            } else {
                right = right.insert(node, depth + 1);
            }
            pushUp();
            return this;
        }

        private static KDNode check(KDNode root, int depth) {
            double limit = root.size * FACTOR;
            if (root.left.size > limit || root.right.size > limit) {
                return refactor(root, depth);
            }
            return root;
        }

        private void init() {
        }

        private static KDNode refactor(KDNode root, int depth) {
            RECORD_LENGTH = 0;
            travel(root);
            return rebuild(0, RECORD_LENGTH, depth);
        }

        private static void travel(KDNode root) {
            if (root == NIL) {
                return;
            }
            travel(root.left);
            RECORD[RECORD_LENGTH++] = root;
            travel(root.right);
        }

        private static KDNode rebuild(int l, int r, int depth) {
            if (l > r) {
                return NIL;
            }
            int m = (l + r) >> 1;
            Sortable.theKthSmallestElement(RECORD, COMPARATORS[depth & 1], l, r, m - l + 1);
            KDNode root = RECORD[m];
            root.init();
            root.left = rebuild(l, m - 1, depth + 1);
            root.right = rebuild(m + 1, r, depth + 1);
            root.pushUp();
            return root;
        }
    }

    public static class Sortable {
        private static final int THRESHOLD = 4;

        public static <T> T theKthSmallestElement(T[] data, Comparator<T> cmp, int f, int t, int k) {
            Memory.swap(data, f, Randomized.nextInt(f, t - 1));
            int l = f;
            int r = t;
            int m = l + 1;
            while (m < r) {
                int c = cmp.compare(data[m], data[l]);
                if (c == 0) {
                    m++;
                } else if (c < 0) {
                    Memory.swap(data, l, m);
                    l++;
                    m++;
                } else {
                    Memory.swap(data, m, --r);
                }
            }
            if (l - f >= k) {
                return theKthSmallestElement(data, cmp, f, l, k);
            } else if (m - f >= k) {
                return data[l];
            }
            return theKthSmallestElement(data, cmp, m, t, k - (m - f));
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
