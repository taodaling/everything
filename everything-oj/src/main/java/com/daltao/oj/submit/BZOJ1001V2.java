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

public class BZOJ1001V2 {
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
        int mod = (int) 1e9 + 7;

        public int mod(int val) {
            val %= mod;
            if (val < 0) {
                val += mod;
            }
            return val;
        }

        public int mod(long val) {
            val %= mod;
            if (val < 0) {
                val += mod;
            }
            return (int) val;
        }

        int bitAt(int x, int i) {
            return (x >> i) & 1;
        }

        int bitAt(long x, int i) {
            return (int) ((x >> i) & 1);
        }

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }


        int n;
        int m;

        Node[] nodes;

        Node nodeAt(int i, int j, int k) {
            return nodes[(i * (n - 1) + j) * (m - 1) + k];
        }

        public void solve() {
            n = io.readInt();
            m = io.readInt();

            nodes = new Node[2 * (n - 1) * (m - 1)];
            for (int i = 0, until = nodes.length; i < until; i++) {
                nodes[i] = new Node();
            }
            Node src = new Node();
            Node dst = new Node();
            for (int i = 0; i < n; i++) {
                for (int j = 1; j < m; j++) {
                    Node a = i == n - 1 ? dst : nodeAt(0, i, j - 1);
                    Node b = i == 0 ? src : nodeAt(1, i - 1, j - 1);
                    addEdge(a, b, io.readInt());
                }
            }

            for (int i = 1; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    Node a = j == 0 ? dst : nodeAt(0, i - 1, j - 1);
                    Node b = j == m - 1 ? src : nodeAt(1, i - 1, j);
                    addEdge(a, b, io.readInt());
                }
            }

            for (int i = 1; i < n; i++) {
                for (int j = 1; j < m; j++) {
                    Node a = nodeAt(0, i - 1, j - 1);
                    Node b = nodeAt(1, i - 1, j - 1);
                    addEdge(a, b, io.readInt());
                }
            }

            MinHeap<Node> heap = new MinHeap(2 + 2 * (n - 1) * (m - 1),
                    new Comparator<Node>() {
                        @Override
                        public int compare(Node o1, Node o2) {
                            return o1.dist - o2.dist;
                        }
                    });

            src.dist = inf;
            src.key = heap.add(src);
            dst.dist = inf;
            dst.key = heap.add(dst);
            for (int i = 0, until = nodes.length; i < until; i++) {
                Node node = nodes[i];
                node.dist = inf;
                node.key = heap.add(node);
            }

            src.dist = 0;
            heap.shiftUpward(src.key);
            while (heap.size > 0) {
                Node head = heap.remove().val;
                if (head == dst) {
                    break;
                }
                for (Edge edge : head.edges) {
                    Node node = edge.another(head);
                    int d = head.dist + edge.len;
                    if (d >= node.dist) {
                        continue;
                    }
                    node.dist = d;
                    heap.shiftUpward(node.key);
                }
            }

            io.cache.append(dst.dist);
        }

        public void addEdge(Node a, Node b, int len) {
            Edge edge = new Edge();
            edge.a = a;
            edge.b = b;
            edge.len = len;
            a.edges.add(edge);
            b.edges.add(edge);
        }
    }

    public static class Node {
        List<Edge> edges = new ArrayList(3);
        MinHeap.Key<Node> key;
        int dist;
    }

    public static class Edge {
        Node a;
        Node b;
        int len;

        Node another(Node me) {
            return a == me ? b : a;
        }

        @Override
        public String toString() {
            return "" + len;
        }
    }

    public static class MinHeap<T> {
        final Key<T>[] data;
        final Comparator<T> comparator;
        int size;
        int cap;

        public MinHeap(int cap, Comparator<T> comparator) {
            this.cap = cap;
            data = new Key[cap + 1];
            this.comparator = comparator;
            size = 0;
        }

        public Key<T> add(T val) {
            Key<T> key = new Key();
            key.val = val;
            data[++size] = key;
            key.index = size;

            shiftUpward(key);

            return key;
        }

        public void shiftUpward(Key<T> key) {
            int index = key.index;
            T val = key.val;
            while (index != 1) {
                int fIndex = index >> 1;
                if (comparator.compare(val, data[fIndex].val) >= 0) {
                    break;
                }
                data[index] = data[fIndex];
                data[index].index = index;
                index = fIndex;
            }
            data[index] = key;
            key.index = index;
        }

        public Key<T> peek() {
            return data[1];
        }

        public Key<T> remove() {
            Key<T> key = data[1];
            data[1] = data[size--];
            data[1].index = 1;

            shiftDownward(data[1]);

            return key;
        }

        public void shiftDownward(Key<T> key) {
            int index = key.index;
            T val = key.val;
            int until = size >> 1;
            while (index <= until) {
                int lIndex = index << 1;
                int rIndex = lIndex + 1;
                int minIndex = rIndex > size || comparator.compare(data[lIndex].val, data[rIndex].val) <= 0 ?
                        lIndex : rIndex;

                if (comparator.compare(val, data[minIndex].val) <= 0) {
                    break;
                }
                data[index] = data[minIndex];
                data[index].index = index;
                index = minIndex;
            }
            data[index] = key;
            key.index = index;
        }

        public int size() {
            return size;
        }

        public static class Key<T> {
            T val;
            int index;
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
