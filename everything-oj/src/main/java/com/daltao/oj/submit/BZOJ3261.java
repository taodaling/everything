package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class BZOJ3261 {
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
            Tree01 root = new Tree01();

            int n = io.readInt();
            int m = io.readInt();

            int offset = 30;
            List<Tree01> list = new ArrayList(n * 2);
            List<Integer> xorList = new ArrayList(n * 2);
            list.add(root);
            xorList.add(0);
            for (int i = 0; i < n; i++) {
                int a = io.readInt() ^ xorList.get(i);
                debug.debug("a", a);
                list.add(Tree01.insert(list.get(i), a, offset));
                xorList.add(a);
            }

            char[] cmd = new char[1];
            for (int i = 0; i < m; i++) {
                io.readString(cmd, 0);
                if (cmd[0] == 'A') {
                    int a = io.readInt() ^ xorList.get(xorList.size() - 1);
                    debug.debug("a", a);
                    list.add(Tree01.insert(list.get(list.size() - 1), a, offset));
                    xorList.add(a);
                } else {
                    int l = Math.max(io.readInt() - 2, 0);
                    int r = io.readInt() - 1;
                    int x = io.readInt() ^ xorList.get(xorList.size() - 1);

                    int max = Tree01.maxXOR(list.get(r), list.get(l), x, offset);
                    io.cache.append(max).append('\n');
                }
            }
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
            long divisor = 1;
            long later = 0;
            while (next >= '0' && next <= '9') {
                divisor = divisor * 10;
                later = later * 10 + next - '0';
                next = read();
            }

            if (num >= 0) {
                return num + (later / (double) divisor);
            } else {
                return num - (later / (double) divisor);
            }
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

    public static class Tree01 implements Cloneable {
        private Tree01[] children = new Tree01[]{NIL, NIL};
        int size;
        private static Tree01 NIL = new Tree01();

        static {
            NIL.children[0] = NIL.children[1] = NIL;
        }

        public void pushDown() {
        }

        public void pushUp() {
            size = children[0].size + children[1].size;
        }

        @Override
        protected Tree01 clone() {
            try {
                Tree01 tree01 = (Tree01) super.clone();
                tree01.children = children.clone();
                return tree01;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public static Tree01 insert(Tree01 root, int v, int offset) {
            root.pushDown();
            if (root == NIL) {
                root = new Tree01();
            } else {
                root = root.clone();
            }
            if (offset < 0) {
                root.size++;
                return root;
            }

            int bit = (v >> offset) & 1;
            root.children[bit] = insert(root.children[bit], v, offset - 1);
            root.pushUp();
            return root;
        }

        public static int maxXOR(Tree01 a, Tree01 b, int mask, int offset) {
            if (offset < 0) {
                return 0;
            }

            a.pushDown();
            b.pushDown();

            int bit = (mask >> offset) & 1;
            int reverse = bit ^ 1;
            if (a.children[reverse].size > b.children[reverse].size) {
                return (1 << offset) | maxXOR(a.children[reverse], b.children[reverse], mask, offset - 1);
            } else {
                return maxXOR(a.children[bit], b.children[bit], mask, offset - 1);
            }
        }

        public static String toString(Tree01 root, StringBuilder builder) {
            if (root == NIL) {
                return "";
            }
            if (root.size == 1 && root.children[0] == NIL && root.children[1] == NIL) {
                return builder.toString() + "\n";
            }
            builder.append('0');
            String result = toString(root.children[0], builder);
            builder.setCharAt(builder.length() - 1, '1');
            result += toString(root.children[1], builder);
            builder.setLength(builder.length() - 1);
            return result;
        }

        @Override
        public String toString() {
            return toString(this, new StringBuilder());
        }
    }

}
