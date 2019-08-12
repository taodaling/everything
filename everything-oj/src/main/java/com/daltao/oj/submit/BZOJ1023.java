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

public class BZOJ1023 {
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

            Node[] nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }

            for (int i = 0; i < m; i++) {
                int k = io.readInt();

                Node last = nodes[io.readInt()];
                for (int j = 1; j < k; j++) {
                    Node next = nodes[io.readInt()];

                    Edge edge = new Edge();
                    edge.a = last;
                    edge.b = next;
                    last.children.add(edge);
                    next.children.add(edge);

                    last = next;
                }
            }


            minIntQueue = new MinIntQueue(n, new IntComparator() {
                @Override
                public int compare(int a, int b) {
                    return -(a - b);
                }
            });
            tarjan(nodes[1], null);

            for (int i = 1; i <= n; i++) {
                if (nodes[i].circle.size() == 2) {
                    throw new RuntimeException();
                }
            }

            dfs(nodes[1], null);

            io.cache.append(nodes[1].pair);
        }


        int order = 1;

        public int order() {
            return order++;
        }

        Deque<Node> stack = new ArrayDeque();

        public void tarjan(Node root, Edge parent) {
            if (root.dfn != 0) {
                return;
            }
            root.dfn = root.low = order();
            root.instk = true;
            stack.addLast(root);

            for (Edge edge : root.children) {
                if (edge == parent) {
                    continue;
                }
                Node node = edge.other(root);
                tarjan(node, edge);
                if (node.instk) {
                    root.low = Math.min(root.low, node.low);
                }
            }

            if (root.low == root.dfn) {
                while (true) {
                    Node tail = stack.pollLast();
                    tail.set = root;
                    tail.instk = false;
                    root.circle.add(tail);
                    if (root == tail) {
                        break;
                    }
                }
            }
        }

        public void dfs0(Node root, Edge parent) {
            root.deepest = 0;
            root.pair = 0;

            for (Edge edge : root.children) {
                if (edge == parent) {
                    continue;
                }
                Node node = edge.other(root);
                if (node.set == root.set) {
                    continue;
                }
                dfs(node, edge);

                root.pair = Math.max(root.pair, root.deepest + node.deepest + 1);
                root.pair = Math.max(root.pair, node.pair);
                root.deepest = Math.max(root.deepest, node.deepest + 1);
            }
        }


        GeqSlopeOptimizer slopeOptimizer = new GeqSlopeOptimizer();
        MinIntQueue minIntQueue;

        public void dfs(Node root, Edge parent) {

            List<Node> circle = root.circle;
            int n = circle.size();

            for (Node node : circle) {
                dfs0(node, parent);
            }

            if (n == 1) {
                return;
            }

            int deepest = 0;
            int pair = 0;

            for (int i = 0; i < n; i++) {
                Node node = circle.get(i);
                deepest = Math.max(deepest, node.deepest + Math.min(i + 1, n - 1 - i));
                pair = Math.max(pair, node.pair);
            }


            minIntQueue.reset();
            int half = n / 2;
            for (int i = 0; i < half; i++) {
                Node node = circle.get(i);
                minIntQueue.enqueue(node.deepest - i);
            }

            for (int i = half; i < n + half; i++) {
                Node node = circle.get(i % n);
                pair = Math.max(pair, minIntQueue.min() + node.deepest + i);
                minIntQueue.deque();
                minIntQueue.enqueue(node.deepest - i);
            }

//            slopeOptimizer.clear();
//            int half = n / 2;
//            for (int i = 0; i < half; i++) {
//                Node node = circle.get(i);
//                slopeOptimizer.add(node.deepest, i, i);
//            }
//
//            for (int i = half; i < n + half; i++) {
//                Node node = circle.get(i % n);
//                slopeOptimizer.since(i - half);
//                int which = slopeOptimizer.getBestChoice(1);
//                pair = Math.max(pair, node.deepest + i + circle.get(which % n).deepest - which);
//                slopeOptimizer.add(node.deepest, i, i);
//            }

            root.pair = pair;
            root.deepest = deepest;
        }
    }


    public static class Edge {
        Node a;
        Node b;

        Node other(Node me) {
            return a == me ? b : a;
        }

        @Override
        public String toString() {
            return String.format("(%s,%s)", a, b);
        }
    }

    public static class Node {
        List<Edge> children = new ArrayList(2);
        List<Node> circle = new ArrayList();
        Node set;
        int id;
        int dfn;
        int low;
        boolean instk;

        int deepest;
        int pair;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class GeqSlopeOptimizer {
        private static class Point {
            final long x;
            final long y;
            final int id;

            private Point(long x, long y, int id) {
                this.x = x;
                this.y = y;
                this.id = id;
            }
        }

        Deque<Point> deque = new ArrayDeque();

        private double slope(Point a, Point b) {
            if (b.x == a.x) {
                if (b.y == a.y) {
                    return 0;
                } else if (b.y > a.y) {
                    return 1e50;
                } else {
                    return 1e-50;
                }
            }
            return (double) (b.y - a.y) / (b.x - a.x);
        }

        Point add(long y, long x, int id) {
            Point t1 = new Point(x, y, id);
            while (deque.size() >= 2) {
                Point t2 = deque.removeLast();
                Point t3 = deque.peekLast();
                if (slope(t3, t2) > slope(t2, t1)) {
                    deque.addLast(t2);
                    break;
                }
            }
            deque.addLast(t1);
            return t1;
        }

        int getBestChoice(long s) {
            while (deque.size() >= 2) {
                Point h1 = deque.removeFirst();
                Point h2 = deque.peekFirst();
                if (slope(h2, h1) < s) {
                    deque.addFirst(h1);
                    break;
                }
            }
            return deque.peekFirst().id;
        }

        public void clear() {
            deque.clear();
        }

        public void since(int id) {
            while (!deque.isEmpty() && deque.peekFirst().id < id) {
                deque.removeFirst();
            }
        }
    }

    public interface IntComparator {
        public int compare(int a, int b);
    }


    public static class IntDeque {
        int[] data;
        int bpos;
        int epos;
        int cap;

        public IntDeque(int cap) {
            this.cap = cap + 1;
            this.data = new int[this.cap];
        }

        public int size() {
            int s = epos - bpos;
            if (s < 0) {
                s += cap;
            }
            return s;
        }

        public boolean isEmpty() {
            return epos == bpos;
        }

        public int peekFirst() {
            return data[bpos];
        }

        private int last(int i) {
            return (i == 0 ? cap : i) - 1;
        }

        private int next(int i) {
            int n = i + 1;
            return n == cap ? 0 : n;
        }

        public int peekLast() {
            return data[last(epos)];
        }

        public int removeFirst() {
            int t = bpos;
            bpos = next(bpos);
            return data[t];
        }

        public int removeLast() {
            return data[epos = last(epos)];
        }

        public void addLast(int val) {
            data[epos] = val;
            epos = next(epos);
        }

        public void addFirst(int val) {
            data[bpos = last(bpos)] = val;
        }

        public void reset() {
            bpos = epos = 0;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = bpos; i != epos; i = next(i)) {
                builder.append(data[i]).append(' ');
            }
            return builder.toString();
        }
    }


    public static class MinIntQueue {
        IntDeque minQueue;
        IntDeque data;
        IntComparator comparator;

        public MinIntQueue(int cap, IntComparator comparator) {
            minQueue = new IntDeque(cap);
            data = new IntDeque(cap);
            this.comparator = comparator;
        }

        public void reset() {
            minQueue.reset();
            data.reset();
        }

        public void enqueue(int val) {
            data.addLast(val);
            while (!minQueue.isEmpty() && comparator.compare(minQueue.peekLast(), val) > 0) {
                minQueue.removeLast();
            }
            minQueue.addLast(val);
        }

        public int deque() {
            int val = data.removeFirst();
            if (minQueue.peekFirst() == val) {
                minQueue.removeFirst();
            }
            return val;
        }

        public int peek() {
            return data.peekFirst();
        }

        public int size() {
            return data.size();
        }

        public int min() {
            return minQueue.peekFirst();
        }

        public boolean isEmpty() {
            return data.isEmpty();
        }

    }


    public static class FastIO {
        public final StringBuilder cache = new StringBuilder(1 << 13);
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 13);
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

        public void flush() throws IOException {
            os.write(cache.toString().getBytes(charset));
            os.flush();
            cache.setLength(0);
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
