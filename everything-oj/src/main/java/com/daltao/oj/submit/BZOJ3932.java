package com.daltao.oj.submit;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BZOJ3932 {
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

            int[][] tasks = new int[n * 2][3];
            int typeIndex = 0;
            int timeIndex = 1;
            int priorityIndex = 2;

            for (int i = 0; i < n; i++) {
                int s = io.readInt();
                int e = io.readInt() + 1;
                int p = io.readInt();

                tasks[i][typeIndex] = 0;
                tasks[i + n][typeIndex] = 1;
                tasks[i][timeIndex] = s;
                tasks[i + n][timeIndex] = e;
                tasks[i][priorityIndex] = p;
                tasks[i + n][priorityIndex] = p;
            }

            Arrays.sort(tasks, new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    return o1[1] - o2[1];
                }
            });

            int lastTime = 0;
            TreapNode lastNode = TreapNode.NIL;

            TreapNode[] status = new TreapNode[n + 2];
            for (int[] task : tasks) {
                if (lastTime != task[timeIndex]) {
                    status[lastTime] = lastNode;
                    lastTime = task[timeIndex];
                }
                if (task[typeIndex] == 0) {
                    TreapNode node = new TreapNode();
                    node.key = task[priorityIndex];
                    node.pushUp();
                    //add
                    TreapNode[] part = TreapNode.splitByKey(lastNode, task[priorityIndex]);
                    part[0] = TreapNode.merge(part[0], node);
                    lastNode = TreapNode.merge(part[0], part[1]);
                } else {
                    TreapNode[] part1 = TreapNode.splitByKey(lastNode, task[priorityIndex] - 1);
                    TreapNode[] part2 = TreapNode.splitByRank(part1[1], 1);
                    part1[1] = part2[1];
                    lastNode = TreapNode.merge(part1[0], part1[1]);
                }
            }

            status[lastTime] = lastNode;

            for (int i = 1; i <= n; i++) {
                if (status[i] == null) {
                    status[i] = status[i - 1];
                }
            }

            int pre = 1;
            for (int i = 0; i < m; i++) {
                int x = io.readInt();
                int a = io.readInt();
                int b = io.readInt();
                int c = io.readInt();

                int k = 1 + (int) (((long) a * pre + b) % c);
                debug.debug("k", k);
                TreapNode node = status[x];
                TreapNode[] part = TreapNode.splitByRank(node, k);
                pre = part[0].sum;
                io.cache.append(pre).append('\n');
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

    public static class TreapNode implements Cloneable {
        private static Random random = new Random();

        private static TreapNode NIL = new TreapNode();

        static {
            NIL.left = NIL.right = NIL;
        }

        TreapNode left = NIL;
        TreapNode right = NIL;
        int size;
        int sum;
        int key;

        @Override
        public TreapNode clone() {
            try {
                return (TreapNode) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public void pushDown() {
        }

        public void pushUp() {
            size = left.size + right.size + 1;
            sum = left.sum + right.sum + key;
        }


        public static TreapNode[] splitByRank(TreapNode root, int rank) {
            if (root == NIL) {
                return new TreapNode[]{NIL, NIL};
            }
            root.pushDown();
            root = root.clone();
            TreapNode[] result;
            if (root.left.size >= rank) {
                result = splitByRank(root.left, rank);
                root.left = result[1];
                result[1] = root;
            } else {
                result = splitByRank(root.right, rank - (root.size - root.right.size));
                root.right = result[0];
                result[0] = root;
            }
            root.pushUp();
            return result;
        }

        public static TreapNode merge(TreapNode a, TreapNode b) {
            if (a == NIL) {
                return b;
            }
            if (b == NIL) {
                return a;
            }
            if (random.nextBoolean()) {
                a.pushDown();
                a = a.clone();
                a.right = merge(a.right, b);
                a.pushUp();
                return a;
            } else {
                b.pushDown();
                b = b.clone();
                b.left = merge(a, b.left);
                b.pushUp();
                return b;
            }
        }

        public static void toString(TreapNode root, StringBuilder builder) {
            if (root == NIL) {
                return;
            }
            root.pushDown();
            toString(root.left, builder);
            builder.append(root.key).append(',');
            toString(root.right, builder);
        }

        public static TreapNode clone(TreapNode root) {
            if (root == NIL) {
                return NIL;
            }
            TreapNode clone = root.clone();
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

        public static TreapNode[] splitByKey(TreapNode root, int key) {
            if (root == NIL) {
                return new TreapNode[]{NIL, NIL};
            }
            root.pushDown();
            root = root.clone();
            TreapNode[] result;
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
