package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Random;

public class POJ3580 {
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

            long[] data = new long[n];
            for (int i = 0; i < n; i++) {
                data[i] = io.readLong();
            }

            TreapNode root = TreapNode.buildFromSortedData(data, 0, n);
            int m = io.readInt();

            char[] cmd = new char[10];
            for (int i = 0; i < m; i++) {
                io.readString(cmd, 0);
                if (cmd[0] == 'A') {
                    //Add
                    int x = io.readInt();
                    int y = io.readInt();
                    long d = io.readInt();
                    TreapNode[] part1 = TreapNode.splitByRank(root, y);
                    TreapNode[] part2 = TreapNode.splitByRank(part1[0], x - 1);
                    part2[1].bulkAdd(d);

                    part1[0] = TreapNode.merge(part2[0], part2[1]);
                    root = TreapNode.merge(part1[0], part1[1]);
                } else if (cmd[0] == 'R' && cmd[3] == 'E') {
                    //Reverse
                    int x = io.readInt();
                    int y = io.readInt();

                    TreapNode[] part1 = TreapNode.splitByRank(root, y);
                    TreapNode[] part2 = TreapNode.splitByRank(part1[0], x - 1);
                    part2[1].bulkReverse();

                    part1[0] = TreapNode.merge(part2[0], part2[1]);
                    root = TreapNode.merge(part1[0], part1[1]);
                } else if (cmd[0] == 'R' && cmd[3] == 'O') {
                    int x = io.readInt();
                    int y = io.readInt();
                    long t = io.readLong();

                    long loop = y - x + 1;
                    t %= loop;
                    if (t < 0) {
                        t += loop;
                    }

                    TreapNode[] part1 = TreapNode.splitByRank(root, y);
                    TreapNode[] part2 = TreapNode.splitByRank(part1[0], x - 1);
                    TreapNode[] part3 = TreapNode.splitByRank(part2[1], (int) (loop - t));

                    part2[1] = TreapNode.merge(part3[1], part3[0]);
                    part1[0] = TreapNode.merge(part2[0], part2[1]);
                    root = TreapNode.merge(part1[0], part1[1]);
                } else if (cmd[0] == 'I') {
                    //Insert
                    int x = io.readInt();
                    int p = io.readInt();

                    TreapNode[] part1 = TreapNode.splitByRank(root, x);

                    TreapNode node = new TreapNode();
                    node.key = p;
                    node.pushUp();

                    part1[0] = TreapNode.merge(part1[0], node);
                    root = TreapNode.merge(part1[0], part1[1]);
                } else if (cmd[0] == 'D') {
                    //Delete
                    int x = io.readInt();
                    TreapNode[] part1 = TreapNode.splitByRank(root, x);
                    TreapNode[] part2 = TreapNode.splitByRank(part1[0], x - 1);

                    part1[0] = part2[0];
                    root = TreapNode.merge(part1[0], part1[1]);
                } else {
                    // Min
                    int x = io.readInt();
                    int y = io.readInt();

                    TreapNode[] part1 = TreapNode.splitByRank(root, y);
                    TreapNode[] part2 = TreapNode.splitByRank(part1[0], x - 1);

                    part2[1].pushDown();
                    long min = part2[1].min;
                    io.cache.append(min).append('\n');

                    part1[0] = TreapNode.merge(part2[0], part2[1]);
                    root = TreapNode.merge(part1[0], part1[1]);
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
            NIL.min = (long) 1e18;
        }

        TreapNode left = NIL;
        TreapNode right = NIL;
        int size;
        long key;
        long bulkAdd;
        boolean bulkReverse;
        long min;

        public static TreapNode buildFromSortedData(long[] data, int l, int r) {
            Deque<TreapNode> deque = new ArrayDeque(r - l);

            for (int i = l; i < r; i++) {
                TreapNode node = new TreapNode();
                node.key = data[i];
                while (!deque.isEmpty()) {
                    if (random.nextBoolean()) {
                        TreapNode tail = deque.removeLast();
                        tail.right = node.left;
                        node.left = tail;
                        tail.pushUp();
                    } else {
                        break;
                    }
                }

                deque.addLast(node);
            }

            TreapNode last = NIL;
            while (!deque.isEmpty()) {
                TreapNode tail = deque.removeLast();
                tail.right = last;
                tail.pushUp();
                last = tail;
            }

            return last;
        }

        public void bulkReverse() {
            bulkReverse = !bulkReverse;
        }

        public void bulkAdd(long v) {
            bulkAdd += v;
            min += v;
        }

        @Override
        public TreapNode clone() {
            try {
                return (TreapNode) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public void pushDown() {
            if (bulkAdd != 0) {
                key += bulkAdd;
                left.bulkAdd(bulkAdd);
                right.bulkAdd(bulkAdd);
                bulkAdd = 0;
            }

            if (bulkReverse) {
                TreapNode tmp = left;
                left = right;
                right = tmp;
                left.bulkReverse();
                right.bulkReverse();
                bulkReverse = false;
            }
        }

        public void pushUp() {
            size = left.size + right.size + 1;
            min = Math.min(Math.min(left.min, right.min), key);
        }

        public static TreapNode[] splitByRank(TreapNode root, int rank) {
            if (root == NIL) {
                return new TreapNode[]{NIL, NIL};
            }
            root.pushDown();
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
    }
}
