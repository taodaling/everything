package com.daltao.oj.old.submit.codeforces;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Random;
import java.util.function.Consumer;

public class CF702F {
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
            int[][] tshirts = new int[n][2];
            for (int i = 0; i < n; i++) {
                tshirts[i][0] = io.readInt();
                tshirts[i][1] = io.readInt();
            }

            Arrays.sort(tshirts, (a, b) -> a[1] != b[1] ? b[1] - a[1] : a[0] - b[0]);

            int m = io.readInt();
            Treap root = Treap.NIL;
            for (int i = 0; i < m; i++) {
                Treap node = new Treap();
                node.id = i;
                node.key = io.readInt();
                root = Treap.insertSingleNode(root, node);
            }

            for (int[] tshirt : tshirts) {
                debug.debug("tshirt", tshirt);
                int price = tshirt[0];
                Treap[] parts = Treap.splitByKey(root, price - 1);
                parts[1].addCnt(1);
                parts[1].subtract(price);
                Treap[] parts2 = Treap.splitByKey(parts[1], price - 1);
                Treap.dfs(parts2[0], node -> {
                    node.left = Treap.NIL;
                    node.right = Treap.NIL;
                    parts[0] = Treap.insertSingleNode(parts[0], node);
                });

                root = Treap.merge(parts[0], parts2[1]);
            }

            int[] count = new int[m];
            Treap.dfs(root, node -> {
                count[node.id] = node.cnt;
            });

            for (int i = 0; i < m; i++) {
                io.cache.append(count[i]).append(' ');
            }
        }
    }


    public static class Treap implements Cloneable {
        private static Random random = new Random(19950823);

        private static Treap NIL = new Treap();

        static {
            NIL.left = NIL.right = NIL;
        }

        Treap left = NIL;
        Treap right = NIL;
        int key;
        int id;
        int subDirty;
        int cnt;
        int cntDirty;

        public static void dfs(Treap root, Consumer<Treap> consumer) {
            if (root == NIL) {
                return;
            }
            root.pushDown();
            dfs(root.left, consumer);
            dfs(root.right, consumer);
            consumer.accept(root);
            root.pushUp();
        }

        public void subtract(int s) {
            subDirty += s;
            key -= s;
        }

        public void addCnt(int cnt) {
            this.cnt += cnt;
            this.cntDirty += cnt;
        }

        @Override
        public Treap clone() {
            try {
                return (Treap) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public void pushDown() {
            if (subDirty != 0) {
                left.subtract(subDirty);
                right.subtract(subDirty);
                subDirty = 0;
            }
            if (cntDirty != 0) {
                left.addCnt(cntDirty);
                right.addCnt(cntDirty);
                cntDirty = 0;
            }
        }

        public void pushUp() {
        }


        public static Treap merge(Treap a, Treap b) {
            if (a == NIL) {
                return b;
            }
            if (b == NIL) {
                return a;
            }
            if (random.nextBoolean()) {
                a.pushDown();
                a.right = merge(a.right, b);
                a.pushUp();
                return a;
            } else {
                b.pushDown();
                b.left = merge(a, b.left);
                b.pushUp();
                return b;
            }
        }

        public static void toString(Treap root, StringBuilder builder) {
            if (root == NIL) {
                return;
            }
            root.pushDown();
            toString(root.left, builder);
            builder.append(root.id).append(',');
            toString(root.right, builder);
        }

        public static Treap clone(Treap root) {
            if (root == NIL) {
                return NIL;
            }
            Treap clone = root.clone();
            clone.left = clone(root.left);
            clone.right = clone(root.right);
            return clone;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder().append(key).append(":");
            toString(clone(this), builder);
            return builder.toString();
        }

        public static Treap[] splitByKey(Treap root, int key) {
            if (root == NIL) {
                return new Treap[]{NIL, NIL};
            }
            root.pushDown();
            Treap[] result;
            if (root.key > key) {
                result = splitByKey(root.left, key);
                root.left = result[1];
                result[1] = root;
            } else {
                result = splitByKey(root.right, key);
                root.right = result[0];
                result[0] = root;
            }
            root.pushUp();
            return result;
        }

        public static Treap insertSingleNode(Treap root, Treap single) {
            Treap[] parts = root.splitByKey(root, single.key);
            parts[0] = merge(parts[0], single);
            return merge(parts[0], parts[1]);
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
