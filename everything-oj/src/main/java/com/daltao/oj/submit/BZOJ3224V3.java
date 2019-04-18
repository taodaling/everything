package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BZOJ3224V3 {
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
            Scapegoat root = Scapegoat.NIL;
            int n = io.readInt();
            for (int i = 0; i < n; i++) {
                int opt = io.readInt();
                int x = io.readInt();
                switch (opt) {
                    case 1:
                        root = root.insert(x);
                        break;
                    case 2:
                        root = root.delete(x);
                        //root.toString();
                        break;
                    case 3:
                        io.cache.append(root.numberOfElementLessThanOrEqualTo(x - 1) + 1).append('\n');
                        break;
                    case 4:
                        io.cache.append(root.theKthSmallestElement(x)).append('\n');
                        break;
                    case 5:
                        io.cache.append(root.theKthSmallestElement(root.numberOfElementLessThanOrEqualTo(x - 1))).append('\n');
                        break;
                    case 6:
                        io.cache.append(root.theKthSmallestElement(root.numberOfElementLessThanOrEqualTo(x) + 1)).append('\n');
                        break;
                }
            }
        }
    }

    public static class Scapegoat implements Cloneable {
        public static final Scapegoat NIL = new Scapegoat();
        public static final double FACTOR = 0.66666;
        public static final List<Scapegoat> RECORDER = new ArrayList();

        Scapegoat left = NIL;
        Scapegoat right = NIL;
        int size;
        int key;
        int count;
        int total;
        int trashed;

        static {
            NIL.left = NIL.right = NIL;
            NIL.size = NIL.key = NIL.count = 0;
        }

        public void pushUp() {
            size = left.size + right.size + 1;
            trashed = left.trashed + right.trashed + (count == 0 ? 1 : 0);
            total = left.total + right.total + count;
        }

        public void pushDown() {
            left = check(left);
            right = check(right);
        }

        private Scapegoat() {
        }

        private Scapegoat(int key) {
            this.key = key;
            this.count = 1;
            pushUp();
        }

        public Scapegoat insert(int x) {
            if (this == NIL) {
                return new Scapegoat(x);
            }
            pushDown();
            if (key == x) {
                count++;
            } else if (key >= x) {
                left = left.insert(x);
            } else {
                right = right.insert(x);
            }
            pushUp();
            return this;
        }

        public Scapegoat delete(int x) {
            if (this == NIL) {
                return NIL;
            }
            pushDown();
            if (key == x) {
                count = Math.max(count - 1, 0);
            } else if (key >= x) {
                left = left.delete(x);
            } else {
                right = right.delete(x);
            }
            pushUp();
            return this;
        }

        @Override
        protected Scapegoat clone() {
            if (this == NIL) {
                return NIL;
            }
            try {
                Scapegoat scapegoat = (Scapegoat) super.clone();
                scapegoat.left = scapegoat.left.clone();
                scapegoat.right = scapegoat.right.clone();
                return scapegoat;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public int numberOfElementLessThanOrEqualTo(int x) {
            if (this == NIL) {
                return 0;
            }
            pushDown();
            int ans;
            if (key == x) {
                ans = total - right.total;
            } else if (key > x) {
                ans = left.numberOfElementLessThanOrEqualTo(x);
            } else {
                ans = total - right.total + right.numberOfElementLessThanOrEqualTo(x);
            }
            pushUp();
            return ans;
        }

        public int theKthSmallestElement(int k) {
            if (this == NIL) {
                return -1;
            }
            pushDown();
            int ans;
            if (k <= left.total) {
                ans = left.theKthSmallestElement(k);
            } else {
                k -= total - right.total;
                if (k <= 0) {
                    ans = key;
                } else {
                    ans = right.theKthSmallestElement(k);
                }
            }
            pushUp();
            return ans;
        }

        private static Scapegoat check(Scapegoat root) {
            double limit = (root.size - root.trashed) * FACTOR;
            if (root.left.size > limit || root.right.size > limit) {
                return refactor(root);
            }
            return root;
        }

        private static Scapegoat refactor(Scapegoat root) {
            RECORDER.clear();
            travel(root);
            return rebuild(0, RECORDER.size() - 1);
        }

        private void init() {
        }

        private static Scapegoat rebuild(int l, int r) {
            if (l > r) {
                return NIL;
            }
            int m = (l + r) >> 1;
            Scapegoat root = RECORDER.get(m);
            root.init();
            root.left = rebuild(l, m - 1);
            root.right = rebuild(m + 1, r);
            root.pushUp();
            return root;
        }

        private static void travel(Scapegoat root) {
            if (root == NIL) {
                return;
            }
            travel(root.left);
            if (root.count > 0) {
                RECORDER.add(root);
            }
            travel(root.right);
        }

        public void toString(StringBuilder builder) {
            if (this == NIL) {
                return;
            }
            pushDown();
            left.toString(builder);
            for (int i = 0; i < count; i++) {
                builder.append(key).append(',');
            }
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
