package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class BZOJ4066 {
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
        int inf = (int) 1e9;

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
            int lastAns = 0;
            KDNode root = KDNode.NIL;
            while (true) {
                int c = io.readInt();
                if (c == 1) {
                    int x = lastAns ^ io.readInt();
                    int y = lastAns ^ io.readInt();
                    int a = lastAns ^ io.readInt();
                    root = root.insert(new KDNode(a, x, y), 0);
                } else if (c == 2) {
                    KDNode.Rect rect = new KDNode.Rect();
                    rect.min[0] = lastAns ^ io.readInt();
                    rect.min[1] = lastAns ^ io.readInt();
                    rect.max[0] = lastAns ^ io.readInt();
                    rect.max[1] = lastAns ^ io.readInt();
                    int ans = root.query(rect, 0);
                    //lastAns = ans;
                    io.cache.append(ans).append('\n');
                } else {
                    break;
                }

            }
        }
    }


    public static class KDNode implements Cloneable {
        public static final KDNode[] RECORD = new KDNode[600000];
        public static final Comparator<KDNode>[] COMPARATORS = new Comparator[2];
        public static final KDNode NIL = new KDNode();
        public static final double FACTOR = 0.75;
        public static final Random RANDOM = new Random(123456789);
        public static final int INF = (int) 1e9;
        public static final int DIMENSION = 2;

        private KDNode() {
        }

        public KDNode(int val, int x, int y) {
            this.val = val;
            this.sum = this.val;
            size = 1;
            position[0] = x;
            position[1] = y;
            pushUp();
        }

        public static class Rect {
            int[] min = new int[DIMENSION];
            int[] max = new int[DIMENSION];

            public void maxRectContain(Rect left, Rect right, int[] position) {
                for (int i = 0; i < DIMENSION; i++) {
                    min[i] = Math.min(left.min[i], right.min[i]);
                    min[i] = Math.min(min[i], position[i]);
                    max[i] = Math.max(left.max[i], right.max[i]);
                    max[i] = Math.max(max[i], position[i]);
                }
            }

            public static boolean intersect(Rect a, Rect b) {
                for (int i = 0; i < DIMENSION; i++) {
                    if (a.max[i] < b.min[i] || a.min[i] > b.max[i]) {
                        return false;
                    }
                }
                return true;
            }

            public static boolean contain(Rect a, Rect b) {
                for (int i = 0; i < DIMENSION; i++) {
                    if (a.max[i] < b.max[i] || a.min[i] > b.min[i]) {
                        return false;
                    }
                }
                return true;
            }

            public static boolean contain(Rect a, int[] pos) {
                for (int i = 0; i < DIMENSION; i++) {
                    if (a.max[i] < pos[i] || a.min[i] > pos[i]) {
                        return false;
                    }
                }
                return true;
            }
        }

        static {
            NIL.position = null;
            NIL.left = NIL.right = NIL;
            NIL.size = 0;
            for (int i = 0; i < DIMENSION; i++) {
                NIL.rect.max[i] = -INF;
                NIL.rect.min[i] = INF;
            }
        }

        static {
            for (int i = 0; i < DIMENSION; i++) {
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
        int[] position = new int[DIMENSION];
        int size = 1;
        int sum;
        int val;
        Rect rect = new Rect();

        public void pushUp() {
            size = left.size + right.size + 1;
            sum = left.sum + right.sum + val;
            rect.maxRectContain(left.rect, right.rect, position);
        }

        public void pushDown(int depth) {
            left = check(left, depth + 1);
            right = check(right, depth + 1);
        }

        public static int distance(int[] a, int[] b) {
            int dis = 0;
            for (int i = 0; i < DIMENSION; i++) {
                dis += Math.abs(a[i] - b[i]);
            }
            return dis;
        }

        public int expectDistance(int[] pos, Rect rect) {
            int total = 0;
            total += Math.max(rect.min[0] - pos[0], 0);
            total += Math.max(rect.min[1] - pos[1], 0);
            total += Math.max(pos[0] - rect.max[0], 0);
            total += Math.max(pos[1] - rect.max[1], 0);
            return total;
        }

        public int query(Rect range, int depth) {
            if (this == NIL) {
                return 0;
            }
            if (!Rect.intersect(range, rect)) {
                return 0;
            }
            if (Rect.contain(range, rect)) {
                return sum;
            }

            pushDown(depth);
            int r = left.query(range, depth + 1)
                    + right.query(range, depth + 1);
            if (Rect.contain(range, position)) {
                r += val;
            }
            return r;
        }

        public KDNode insert(KDNode node, int depth) {
            if (this == NIL) {
                node.pushUp();
                return node;
            }
            pushDown(depth);
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
            return rebuild(0, RECORD_LENGTH - 1, depth);
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
            Sortable.theKthSmallestElement(RECORD, COMPARATORS[depth & 1], l, r + 1, m - l + 1);
            KDNode root = RECORD[m];
            root.init();
            root.left = rebuild(l, m - 1, depth + 1);
            root.right = rebuild(m + 1, r, depth + 1);
            root.pushUp();
            return root;
        }

        @Override
        protected KDNode clone() {
            if (this == NIL) {
                return NIL;
            }
            try {
                KDNode node = (KDNode) super.clone();
                node.rect = new Rect();
                node.left = node.left.clone();
                node.right = node.right.clone();
                node.pushUp();
                return node;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        private void toString(StringBuilder builder) {
            if (this == NIL) {
                return;
            }
            //pushDown(0);
            left.toString(builder);
            builder.append(String.format("(%d,%d),", position[0], position[1]));
            right.toString(builder);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            clone().toString(builder);
            if (builder.length() > 0) {
                builder.setLength(builder.length() - 1);
            }
            return builder.toString();
        }
    }

    public static class Randomized {
        static Random random = new Random(123456789);

        public static double nextDouble(double min, double max) {
            return random.nextDouble() * (max - min) + min;
        }

        public static void randomizedArray(int[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                int tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(long[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                long tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(double[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                double tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(float[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                float tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static <T> void randomizedArray(T[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                T tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static int nextInt(int l, int r) {
            return random.nextInt(r - l + 1) + l;
        }
    }


    public static class Sortable {
        private static final int THRESHOLD = 0;

        public static <T> void insertSort(T[] data, Comparator<T> cmp, int f, int t) {
            for (int i = f + 1; i < t; i++) {
                int j = i;
                T val = data[i];
                while (j > f && cmp.compare(data[j - 1], val) > 0) {
                    data[j] = data[j - 1];
                    j--;
                }
                data[j] = val;
            }
        }

        public static <T> T theKthSmallestElement(T[] data, Comparator<T> cmp, int f, int t, int k) {
            if (t - f == THRESHOLD) {
                insertSort(data, cmp, f, t);
                return data[f + k - 1];
            }
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
