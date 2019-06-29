package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class LUOGU4716 {
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
            int r = io.readInt() - 1;
            DirectMinSpanningTree tree = new DirectMinSpanningTree(n);
            for (int i = 0; i < m; i++) {
                int u = io.readInt() - 1;
                int v = io.readInt() - 1;
                int w = io.readInt();
                tree.addEdge(u, v, w);
            }

            tree.contract();
            List<DirectMinSpanningTree.Edge> edges = tree.dismantle(r);
            long sum = 0;
            for (DirectMinSpanningTree.Edge edge : edges) {
                if (edge.weight == Long.MAX_VALUE) {
                    io.cache.append(-1);
                    return;
                }
                sum += edge.weight;
            }
            io.cache.append(sum);
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

    public static class DirectMinSpanningTree {
        long inf = Long.MAX_VALUE;
        int now;
        Node top;

        public static class Edge {
            Node src;
            Node dst;
            long weight;
            long fixedWeight;

            @Override
            public String toString() {
                return "(" + src + "," + dst + ")[" + weight + "]";
            }
        }

        public static class Node {
            int id = -1;
            List<Edge> inEdges = new ArrayList<>(2);
            LeftSideTree queue = LeftSideTree.NIL;
            Node parent;
            Edge outEdge;
            Node outNode;
            int visited;


            Node circleP = this;
            int circleRank;

            Node currentLevel = this;

            Node find() {
                return circleP.circleP == circleP ? circleP : (circleP = circleP.find());
            }

            static void merge(Node a, Node b) {
                a = a.find();
                b = b.find();
                if (a == b) {
                    return;
                }
                if (a.circleRank == b.circleRank) {
                    a.circleRank++;
                }
                if (a.circleRank > b.circleRank) {
                    b.circleP = a;
                } else {
                    a.circleP = b;
                }
            }

            @Override
            public String toString() {
                return "" + id;
            }
        }

        public List<Edge> dismantle(int rootId) {
            if (nodes.length == 1) {
                return Collections.emptyList();
            }
            now++;
            Node root = nodes[rootId];
            List<Edge> result = new ArrayList<>();
            dismantle0(root, result);
            return result;
        }

        private void dismantle0(Node root, List<Edge> result) {
            if (root == top || root.visited == now) {
                return;
            }
            root.visited = now;
            Node trace = root;
            while (true) {
                Node front = trace.outNode;
                Node frontRoot = trace.outEdge.dst;
                if (front == root) {
                    break;
                }
                result.add(trace.outEdge);
                front.visited = now;
                dismantle0(frontRoot, result);
                trace = front;
            }
            dismantle0(root.parent, result);
        }

        public void contract() {
            now++;
            Deque<LeftSideTree> deque = new ArrayDeque<>();
            for (Node node : nodes) {
                for (Edge edge : node.inEdges) {
                    edge.fixedWeight = edge.weight;
                    deque.addLast(new LeftSideTree(edge));
                }
                node.queue = LeftSideTree.createFromDeque(deque);
            }

            Deque<Node> stack = new ArrayDeque<>();
            stack.addLast(nodes[0]);
            nodes[0].visited = now;
            int remain = nodes.length;
            while (remain > 1) {
                Node tail = stack.peekLast().currentLevel;
                Edge minInEdge = null;
                while (true) {
                    minInEdge = tail.queue.peek();
                    tail.queue = LeftSideTree.pop(tail.queue);
                    //self loop
                    if (minInEdge.src.find() != minInEdge.dst.find()) {
                        break;
                    }
                }

                Node x = minInEdge.src.find().currentLevel;
                //No loop
                if (x.visited != now) {
                    x.visited = now;
                    x.outEdge = minInEdge;
                    stack.addLast(x);
                    continue;
                }
                //Find loop, merge them together
                Node p = new Node();
                p.visited = now;
                p.outEdge = x.outEdge;
                x.outEdge = minInEdge;
                Node last = x;
                while (true) {
                    Node t = stack.removeLast();
                    t.parent = p;
                    last.outNode = t;
                    t.queue.modify(-last.outEdge.fixedWeight);
                    p.queue = LeftSideTree.merge(t.queue, p.queue);
                    Node.merge(p, t);
                    last = t;
                    remain--;
                    if (t == x) {
                        break;
                    }
                }
                p.find().currentLevel = p;
                stack.addLast(p);
                remain++;
            }
            top = stack.removeLast();
        }

        private Node[] nodes;

        public void addEdge(int s, int t, long weight) {
            Edge edge = new Edge();
            edge.src = nodes[s];
            edge.dst = nodes[t];
            edge.weight = weight;
            edge.dst.inEdges.add(edge);
        }

        public DirectMinSpanningTree(int n) {
            nodes = new Node[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }
            for (int i = 0; i < n; i++) {
                addEdge(i, (i + 1) % n, inf);
            }
        }

        public static class LeftSideTree {
            public static final LeftSideTree NIL = new LeftSideTree(null);

            static {
                NIL.left = NIL;
                NIL.right = NIL;
                NIL.dist = -1;
            }

            LeftSideTree left = NIL;
            LeftSideTree right = NIL;
            int dist;
            DirectMinSpanningTree.Edge key;
            long mod;

            public void pushDown() {
                if (mod != 0) {
                    left.modify(mod);
                    right.modify(mod);
                    mod = 0;
                }
            }

            public void modify(long k) {
                if (this == NIL) {
                    return;
                }
                key.fixedWeight += k;
                mod += k;
            }

            public LeftSideTree(DirectMinSpanningTree.Edge key) {
                this.key = key;
            }

            public static LeftSideTree createFromDeque(Deque<LeftSideTree> deque) {
                while (deque.size() > 1) {
                    deque.addLast(merge(deque.removeFirst(), deque.removeFirst()));
                }
                return deque.removeLast();
            }

            public static LeftSideTree merge(LeftSideTree a, LeftSideTree b) {
                if (a == NIL) {
                    return b;
                } else if (b == NIL) {
                    return a;
                }
                a.pushDown();
                b.pushDown();
                if (a.key.fixedWeight > b.key.fixedWeight) {
                    LeftSideTree tmp = a;
                    a = b;
                    b = tmp;
                }
                a.right = merge(a.right, b);
                if (a.left.dist < a.right.dist) {
                    LeftSideTree tmp = a.left;
                    a.left = a.right;
                    a.right = tmp;
                }
                a.dist = a.right.dist + 1;
                return a;
            }

            public DirectMinSpanningTree.Edge peek() {
                return key;
            }

            public static LeftSideTree pop(LeftSideTree root) {
                root.pushDown();
                return merge(root.left, root.right);
            }
        }
    }
}
