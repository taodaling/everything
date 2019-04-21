package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class BZOJ3569 {
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
        Random random = new Random();

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
            Node[] nodes = new Node[n + 1];
            Edge[] edges = new Edge[m + 1];
            for (int i = 0; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }

            for (int i = 1; i <= m; i++) {
                edges[i] = new Edge();
                edges[i].a = nodes[io.readInt()];
                edges[i].b = nodes[io.readInt()];
                edges[i].xor = random.nextLong();
                edges[i].a.edgeList.add(edges[i]);
                edges[i].b.edgeList.add(edges[i]);
            }
            edges[0] = new Edge();
            edges[0].a = nodes[0];
            edges[0].b = nodes[1];
            nodes[0].edgeList.add(edges[0]);

            Deque<Node> deque = new ArrayDeque(n);
            Deque<Node> dfsStack = new ArrayDeque(n);
            dfsStack.addLast(nodes[0]);
            while (!dfsStack.isEmpty()) {
                Node head = dfsStack.removeFirst();
                deque.addLast(head);
                for (Edge edge : head.edgeList) {
                    Node node = edge.another(head);
                    if(node.visited)
                    {
                        continue;
                    }
                    node.visited = true;
                    dfsStack.addLast(node);
                    node.toFather = edge;
                    edge.inTree = true;
                }
            }

            for (int i = 1; i <= m; i++) {
                if (edges[i].inTree) {
                    continue;
                }
                edges[i].a.xor ^= edges[i].xor;
                edges[i].b.xor ^= edges[i].xor;
            }
            deque.removeFirst();
            boolean connected = deque.size() == n;
            while (!deque.isEmpty()) {
                Node last = deque.removeLast();
                Node father = last.toFather.another(last);
                father.xor ^= last.xor;
            }
            for (int i = 1; i <= m; i++) {
                if (!edges[i].inTree) {
                    continue;
                }
                edges[i].xor = edges[i].a.toFather == edges[i] ?
                        edges[i].a.xor : edges[i].b.xor;
            }
            int q = io.readInt();
            int lastAns = 0;
            LinearBasis linearBasis = new LinearBasis();
            for (int i = 0; i < q; i++) {
                int k = io.readInt();
                linearBasis.clear();
                for (int j = 0; j < k; j++) {
                    linearBasis.add(edges[lastAns ^ io.readInt()].xor);
                }
                boolean ans = linearBasis.size() == k && connected;
                lastAns += ans ? 1 : 0;
                io.cache.append(ans ? "Connected" : "Disconnected").append('\n');
            }
        }
    }

    public static class LinearBasis {
        private long[] map = new long[64];
        private int size;

        public int size() {
            return size;
        }

        public void clear() {
            size = 0;
            Arrays.fill(map, 0);
        }

        public boolean add(long val) {
            for (int i = 0; i < 64 && val != 0; i++) {
                if (bitAt(val, i) == 0) {
                    continue;
                }
                if (map[i] == 0) {
                    map[i] = val;
                    size++;
                    return true;
                }
                val ^= map[i];
            }
            return false;
        }

        private long bitAt(long val, int i) {
            return (val >>> i) & 1;
        }

        public long eliminate(long val) {
            for (int i = 0; i < 64 && val != 0; i++) {
                if (bitAt(val, i) == 0) {
                    continue;
                }
                if (map[i] == 0) {
                    break;
                }
                val ^= map[i];
            }
            return val;
        }
    }

    public static class Node {
        List<Edge> edgeList = new ArrayList();
        Edge toFather;
        boolean visited;
        long xor;
        int id;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class Edge {
        Node a;
        Node b;
        long xor;
        boolean inTree;

        public Node another(Node x) {
            return a == x ? b : a;
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
