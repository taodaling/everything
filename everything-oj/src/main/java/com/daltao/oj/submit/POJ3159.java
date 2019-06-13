package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class POJ3159 {
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
            DifferenceConstraintSystem dcs = new DifferenceConstraintSystem(n);
            for (int i = 0; i < m; i++) {
                dcs.differenceGreaterThanOrEqualTo(io.readInt() - 1, io.readInt() - 1, -io.readInt());
            }
            long ans = dcs.findMaxDifferenceBetween(n - 1, 0);
            io.cache.append(ans);
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

    public static class DifferenceConstraintSystem {
        private static class Node {
            List<Edge> edges = new ArrayList(2);
            long dist;
            boolean inque;
            int times;
            int id;

            @Override
            public String toString() {
                return "a" + id;
            }
        }

        private static class Edge {
            final Node src;
            final Node next;
            final long len;

            private Edge(Node src, Node next, long len) {
                this.src = src;
                this.next = next;
                this.len = len;
            }

            @Override
            public String toString() {
                return String.format("%s - %s <= %d", next.toString(), src.toString(), len);
            }
        }

        Node[] nodes;
        Deque<Node> deque;
        int n;


        public DifferenceConstraintSystem(int n) {
            this.n = n;
            deque = new ArrayDeque(n);
            nodes = new Node[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }
        }

        public void clear(int n) {
            this.n = n;
            for (int i = 0; i < n; i++) {
                nodes[i].edges.clear();
            }
        }

        public void differenceLessThanOrEqualTo(int i, int j, long d) {
            nodes[j].edges.add(new Edge(nodes[j], nodes[i], d));
        }

        public void differenceGreaterThanOrEqualTo(int i, int j, long d) {
            differenceLessThanOrEqualTo(j, i, -d);
        }

        public void differenceEqualTo(int i, int j, long d) {
            differenceGreaterThanOrEqualTo(i, j, d);
            differenceLessThanOrEqualTo(i, j, d);
        }

        public void differenceLessThan(int i, int j, long d) {
            differenceLessThanOrEqualTo(i, j, d - 1);
        }

        public void differenceGreaterThan(int i, int j, long d) {
            differenceGreaterThanOrEqualTo(i, j, d + 1);
        }

        boolean hasSolution;

        private boolean dijkstraElog2V() {
            TreeSet<Node> heap = new TreeSet(new Comparator<Node>() {
                @Override
                public int compare(Node a, Node b) {
                    return a.dist == b.dist ? a.id - b.id : a.dist < b.dist ? -1 : 1;
                }
            });
            heap.addAll(deque);
            while (!heap.isEmpty()) {
                Node head = heap.pollFirst();
                for (Edge edge : head.edges) {
                    Node node = edge.next;
                    if (node.dist <= head.dist + edge.len) {
                        continue;
                    }
                    heap.remove(node);
                    node.dist = head.dist + edge.len;
                    heap.add(node);
                }
            }
            return true;
        }

        private boolean spfa() {
            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                head.inque = false;
                if (head.times >= n) {
                    return false;
                }
                for (Edge edge : head.edges) {
                    Node node = edge.next;
                    if (node.dist <= edge.len + head.dist) {
                        continue;
                    }
                    node.dist = edge.len + head.dist;
                    if (node.inque) {
                        continue;
                    }
                    node.times++;
                    node.inque = true;
                    deque.addLast(node);
                }
            }
            return true;
        }

        public long possibleSolutionOf(int i) {
            return nodes[i].dist;
        }

        private void prepare(long initDist) {
            deque.clear();
            for (int i = 0; i < n; i++) {
                nodes[i].dist = initDist;
                nodes[i].times = 0;
            }
        }

        public boolean hasSolution() {
            prepare(0);
            for (int i = 0; i < n; i++) {
                nodes[i].inque = true;
                deque.addLast(nodes[i]);
            }
            hasSolution = spfa();
            return hasSolution;
        }

        public static final long INF = (long) 2e18;

        /**
         * Find max(ai - aj), if INF is returned, it means no constraint between ai and aj
         */
        public long findMaxDifferenceBetween(int i, int j) {
            prepare(INF);
            deque.addLast(nodes[j]);
            nodes[j].dist = 0;
            nodes[j].inque = true;
            spfa();
            return nodes[i].dist;
        }

        /**
         * Find min(ai - aj), if INF is returned, it means no constraint between ai and aj
         */
        public long findMinDifferenceBetween(int i, int j) {
            long r = findMaxDifferenceBetween(j, i);
            if (r == INF) {
                return INF;
            }
            return -r;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                for (Edge edge : nodes[i].edges) {
                    builder.append(edge).append('\n');
                }
            }
            builder.append("-------------\n");
            if (!hasSolution) {
                builder.append("impossible");
            } else {
                for (int i = 0; i < n; i++) {
                    builder.append("a").append(i).append("=").append(nodes[i].dist).append('\n');
                }
            }
            return builder.toString();
        }
    }
}
