package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Random;

public class BZOJ3224 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("D:\\DATABASE\\TESTCASE\\Code.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io);

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

        public Task(FastIO io) {
            this.io = io;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            int n = io.readInt();

            TreapNode root = TreapNode.NIL;
            for (int i = 0; i < n; i++) {
                int cmd = io.readInt();
                int x = io.readInt();
                switch (cmd) {
                    case 1: {
                        //insert
                        TreapNode[] part1 = TreapNode.split(root, x);
                        TreapNode[] part2 = TreapNode.split(part1[0], x - 1);

                        if (part2[1] == TreapNode.NIL) {
                            part2[1] = new TreapNode();
                            part2[1].key = x;
                        }
                        part2[1].cnt++;
                        part2[1].size++;

                        root = TreapNode.merge(part2[0], part2[1]);
                        root = TreapNode.merge(root, part1[1]);
                    }
                    break;
                    case 2: {
                        //delete
                        TreapNode[] part1 = TreapNode.split(root, x);
                        TreapNode[] part2 = TreapNode.split(part1[0], x - 1);
                        if (part2[1] != TreapNode.NIL) {
                            part2[1].cnt--;
                            part2[1].size--;
                            if (part2[1].cnt == 0) {
                                part2[1] = TreapNode.NIL;
                            }
                        }

                        root = TreapNode.merge(part2[0], part2[1]);
                        root = TreapNode.merge(root, part1[1]);
                    }
                    break;
                    case 3: {
                        //query rank
                        TreapNode[] part1 = TreapNode.split(root, x - 1);
                        io.cache.append(part1[0].size + 1).append('\n');
                        root = TreapNode.merge(part1[0], part1[1]);
                        break;
                    }
                    case 4: {
                        //query k-th
                        int k = x;
                        TreapNode trace = root;
                        while (true) {
                            if (trace.left.size >= k) {
                                trace = trace.left;
                            } else {
                                k -= trace.size - trace.right.size;
                                if (k <= 0) {
                                    break;
                                } else {
                                    trace = trace.right;
                                }
                            }
                        }

                        io.cache.append(trace.key).append('\n');
                    }
                    break;
                    case 5: {
                        TreapNode[] part1 = TreapNode.split(root, x - 1);

                        TreapNode trace = part1[0];
                        while (trace.right != TreapNode.NIL) {
                            trace = trace.right;
                        }

                        io.cache.append(trace.key).append('\n');

                        root = TreapNode.merge(part1[0], part1[1]);
                        break;
                    }
                    case 6: {
                        TreapNode[] part1 = TreapNode.split(root, x);

                        TreapNode trace = part1[1];
                        while (trace.left != TreapNode.NIL) {
                            trace = trace.left;
                        }

                        io.cache.append(trace.key).append('\n');

                        root = TreapNode.merge(part1[0], part1[1]);

                        break;
                    }
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

    public static class TreapNode implements Cloneable {
        private static Random random = new Random();
        private static TreapNode NIL = new TreapNode();

        static {
            NIL.left = NIL.right = NIL;
        }

        TreapNode left = NIL;
        TreapNode right = NIL;
        int key;
        int cnt;
        int size;

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
            size = cnt + left.size + right.size;
        }

        private static TreapNode[] split(TreapNode root, int key) {
            if (root == NIL) {
                return new TreapNode[]{NIL, NIL};
            }
            root.pushDown();
            TreapNode[] trees;
            if (root.key > key) {
                trees = split(root.left, key);
                root.left = trees[1];
                trees[1] = root;
            } else {
                trees = split(root.right, key);
                root.right = trees[0];
                trees[0] = root;
            }
            root.pushUp();
            return trees;
        }

        private static TreapNode merge(TreapNode a, TreapNode b) {
            if (a == NIL) {
                return b;
            }
            if (b == NIL) {
                return a;
            }
            if (random.nextBoolean()) {
                TreapNode tmp = a;
                a = b;
                b = tmp;
            }
            a.pushDown();
            if (a.key >= b.key) {
                a.left = merge(a.left, b);
            } else {
                a.right = merge(a.right, b);
            }
            a.pushUp();
            return a;
        }
    }

}
