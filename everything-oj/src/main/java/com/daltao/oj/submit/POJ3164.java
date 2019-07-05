package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class POJ3164 {
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
            while (io.hasMore())
                solve();
        }

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();
            if (n == 0 && m == 0) {
                return;
            }
            DirectMinSpanningTree dmst = new DirectMinSpanningTree(n);
            int[][] xy = new int[n][2];
            for (int i = 0; i < n; i++) {
                xy[i][0] = io.readInt();
                xy[i][1] = io.readInt();
            }

            for (int i = 0; i < m; i++) {
                int a = io.readInt() - 1;
                int b = io.readInt() - 1;
                if (a < 0 || b < 0) {
                    continue;
                }
                double dx = xy[a][0] - xy[b][0];
                double dy = xy[a][1] - xy[b][1];
                double dist = Math.sqrt(dx * dx + dy * dy);
                dmst.addEdge(a, b, dist);
            }
            dmst.contract();
            List<DirectMinSpanningTree.Edge> edges = dmst.dismantle(0);

            double sum = 0;
            for (DirectMinSpanningTree.Edge edge : edges) {
                if (edge.weight >= 1e50) {
                    io.cache.append("poor snoopy\n");
                    return;
                }
                sum += edge.weight;
            }

            io.cache.append(String.format("%.2f", sum)).append('\n');
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
        double inf = 1e50;
        int now;
        Node top;

        public static class Edge {
            Node src;
            Node dst;
            double weight;
            double fixWeight;

            @Override
            public String toString() {
                return "(" + src + "," + dst + ")[" + weight + "]";
            }
        }

        public static class Node {
            int id = -1;
            List<Edge> outEdges = new ArrayList(2);
            List<Edge> inEdges = new ArrayList(2);
            LeftSideTree queue = LeftSideTree.NIL;
            Node parent;
            Edge out;
            Node outNode;
            int visited;


            Node circleP = this;
            int circleRank;

            Node proxy = this;

            Node findCircle() {
                return circleP.circleP == circleP ? circleP : (circleP = circleP.findCircle());
            }

            static void mergeCircle(Node a, Node b) {
                a = a.findCircle();
                b = b.findCircle();
                if (a == b) {
                    return;
                }
                if (a.circleRank == b.circleRank) {
                    a.circleRank++;
                }
                if (a.circleRank > b.circleRank) {
                    b.circleP = a;
                    a.queue = LeftSideTree.merge(a.queue, b.queue);
                } else {
                    a.circleP = b;
                    b.queue = LeftSideTree.merge(a.queue, b.queue);
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
            List<Edge> result = new ArrayList();
            dfs(root, result);
            return result;
        }

        private void dfs(Node root, List<Edge> result) {
            if (root == top || root.visited == now) {
                return;
            }
            root.visited = now;
            Node trace = root;
            while (true) {
                Node bottom = trace.out.dst;
                Node next = trace.outNode;
                if (next == root) {
                    break;
                }
                result.add(trace.out);
                next.visited = now;
                dfs(bottom, result);
                trace = next;
            }

            dfs(root.parent, result);
        }

        public void contract() {
            now++;
            Deque<LeftSideTree> deque = new ArrayDeque();
            for (Node node : nodes) {
                for (Edge edge : node.inEdges) {
                    edge.fixWeight = edge.weight;
                    deque.addLast(new LeftSideTree(edge));
                }
                node.queue = LeftSideTree.createFromDeque(deque);
            }

            int remain = nodes.length;
            Deque<Node> stack = new ArrayDeque();
            List<Node> waitList = new ArrayList();
            stack.addLast(nodes[0]);
            stack.peekFirst().visited = now;
            while (remain > 1) {
                Node tail = stack.peekLast().findCircle();
                Edge out = null;
                while (out == null) {
                    Edge min = tail.queue.peek();
                    tail.queue = LeftSideTree.pop(tail.queue);
                    //self loop
                    if (min.src.findCircle() == min.dst.findCircle()) {
                        continue;
                    }
                    out = min;
                }

                Node src = out.src.findCircle().proxy;
                //No loop
                if (src.visited != now) {
                    src.visited = now;
                    src.out = out;
                    stack.addLast(src);
                    continue;
                }
                //Find loop, merge them together
                Node proxy = new Node();
                proxy.visited = now;
                proxy.out = src.out;
                src.out = out;
                Node last = src;
                while (true) {
                    Node trace = stack.removeLast().findCircle().proxy;
                    trace.parent = proxy;
                    last.outNode = trace;
                    trace.findCircle().queue.modify(-last.out.fixWeight);
                    Node.mergeCircle(proxy, trace);
                    remain--;
                    last = trace;
                    if (trace == src) {
                        break;
                    }
                }
                proxy.findCircle().proxy = proxy;
                stack.addLast(proxy);
                remain++;
            }
            top = stack.removeLast();
        }

        private Node[] nodes;

        public void addEdge(int s, int t, double weight) {
            Edge edge = new Edge();
            edge.src = nodes[s];
            edge.dst = nodes[t];
            edge.weight = weight;
            edge.src.outEdges.add(edge);
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
            double mod;

            public void pushDown() {
                if (mod != 0) {
                    left.modify(mod);
                    right.modify(mod);
                    mod = 0;
                }
            }

            public void modify(double k) {
                if (this == NIL) {
                    return;
                }
                key.fixWeight += k;
                mod += k;
            }


            public LeftSideTree(DirectMinSpanningTree.Edge key) {
                this.key = key;
            }

            public static LeftSideTree createFromCollection(Collection<LeftSideTree> trees) {
                return createFromDeque(new ArrayDeque(trees));
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
                if (a.key.fixWeight > b.key.fixWeight) {
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

            public boolean isEmpty() {
                return this == NIL;
            }

            public DirectMinSpanningTree.Edge peek() {
                return key;
            }

            public static LeftSideTree pop(LeftSideTree root) {
                root.pushDown();
                return merge(root.left, root.right);
            }

            private void toStringDfs(StringBuilder builder) {
                if (this == NIL) {
                    return;
                }
                builder.append(key).append(' ');
                left.toStringDfs(builder);
                right.toStringDfs(builder);
            }

            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                toStringDfs(builder);
                return builder.toString();
            }
        }
    }
}
