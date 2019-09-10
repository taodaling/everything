package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

public class LOJ2553 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getSecurityManager() == null;
        boolean async = true;

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
        long lInf = (long) 1e18;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        int n;
        Node[] tree1;
        Node[] tree2;
        SparseTable<Node> st1;
        SparseTable<Node> st2;
        List<Node> trace1;
        List<Node> trace2;
        Comparator<Node> sortByOpen = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.open - o2.open;
            }
        };

        public void solve() {
            n = io.readInt();
            tree1 = new Node[n + 1];
            tree2 = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                tree1[i] = new Node();
                tree1[i].id = i;
                tree2[i] = new Node();
                tree2[i].id = i;
            }
            for (int i = 2; i <= n; i++) {
                Node x = tree1[io.readInt()];
                Node y = tree1[io.readInt()];
                Edge edge = new Edge(x, y, io.readLong());
                x.edgeList.add(edge);
                y.edgeList.add(edge);
            }
            for (int i = 2; i <= n; i++) {
                Node x = tree2[io.readInt()];
                Node y = tree2[io.readInt()];
                Edge edge = new Edge(x, y, io.readLong());
                x.edgeList.add(edge);
                y.edgeList.add(edge);
            }

            trace1 = new ArrayList(n * 2);
            trace2 = new ArrayList(n * 2);
            nodeBuf = new ArrayList(n);
            deque = new ArrayDeque(n);
            dfs(tree1[1], null, 0, trace1, true, 1);
            dfs(tree2[1], null, 0, trace2, false, 1);

            st1 = new SparseTable(trace1.toArray(), trace1.size(),
                    sortByOpen);
            st2 = new SparseTable(trace2.toArray(), trace2.size(),
                    sortByOpen);

            long ans = dac(tree1[1]);
            io.cache.append(ans);
        }

        public void expand(Node root) {
            Node trace = root;
            while (trace.edgeList.size() > 2) {
                Node clone = new Node();
                clone.edgeList = trace.edgeList;
                trace.edgeList = new ArrayList(2);
                trace.edgeList.add(clone.edgeList.get(clone.edgeList.size() - 1));
                clone.edgeList.remove(clone.edgeList.size() - 1);
                trace.edgeList.add(new Edge(trace, clone, 0));
                for (Edge e : trace.edgeList) {
                    e.replace(root, trace);
                }
                trace = clone;
            }
            for (Edge e : trace.edgeList) {
                e.replace(root, trace);
            }
        }

        int time = 0;
        List<Node> nodeBuf;
        Deque<Node> deque;

        public long dac(Node root) {
            calcSize(root);
            if (root.size <= 1) {
                trace1.clear();
                collect(root, trace1);
                long ans = -lInf;
                for (Node x : trace1) {
                    for (Node y : trace1) {
                        if (x == y) {
                            continue;
                        }
                        Node opx = tree2[x.id];
                        Node opy = tree2[y.id];
                        Node lca1 = st1.query(Math.min(x.open, y.open), Math.max(x.close, y.close));
                        Node lca2 = st2.query(Math.min(opx.open, opy.open), Math.max(opx.close, opy.close));
                        ans = Math.max(ans, opx.prefix + opy.prefix - lca1.prefix - lca2.prefix);
                    }
                }
                return ans;

            }
            time++;


            Edge center = findCenter(root, root.size * 2 / 3);
            Node higher = center.higher();
            Node lower = center.lower();
            higher.edgeList.remove(center);

            trace1.clear();
            trace2.clear();
            nodeBuf.clear();
            deque.clear();
            collect(root, trace1);
            collect(lower, trace2);

            for (Node node : trace1) {
                Node op = tree2[node.id];
                op.type = 0;
                op.time = time;
                Node lca = st1.query(Math.min(node.open, lower.open),
                        Math.max(node.close, lower.close));
                op.fixWeight = node.prefix - lca.prefix;
                nodeBuf.add(op);
            }
            for (Node node : trace2) {
                Node op = tree2[node.id];
                op.type = 1;
                op.time = time;
                op.fixWeight = node.prefix;
                nodeBuf.add(op);
            }
            int size = nodeBuf.size();
            nodeBuf.sort(sortByOpen);
            for (int i = 1; i < size; i++) {
                Node last = nodeBuf.get(i - 1);
                Node next = nodeBuf.get(i);
                Node lca = st2.query(Math.min(last.open, next.open),
                        Math.max(last.close, next.close));
                if (lca.time != time) {
                    lca.time = time;
                    lca.type = 2;
                    nodeBuf.add(lca);
                }
            }

            //build virtual tree
            nodeBuf.sort(sortByOpen);
            for (Node node : nodeBuf) {
                node.dp0 = -lInf;
                node.dp1 = -lInf;

                if (node.type == 0) {
                    node.dp0 = Math.max(node.dp0, node.fixWeight);
                }
                if (node.type == 1) {
                    node.dp1 = Math.max(node.dp1, node.fixWeight);
                }

                while (!deque.isEmpty()) {
                    Node tail = deque.removeLast();
                    if (tail.close >= node.close) {
                        deque.addLast(tail);
                        break;
                    }
                }
                if (!deque.isEmpty()) {
                    node.virtualFather = deque.peekLast();
                } else {
                    node.virtualFather = null;
                }
                deque.addLast(node);
            }


            long ans = -lInf;
            for (int i = nodeBuf.size() - 1; i >= 0; i--) {
                Node node = nodeBuf.get(i);
                if (node.virtualFather == null) {
                    continue;
                }

                ans = Math.max(ans, node.dp0 + node.virtualFather.dp1 -
                        node.virtualFather.prefix);
                ans = Math.max(ans, node.dp1 + node.virtualFather.dp0 -
                        node.virtualFather.prefix);

                node.virtualFather.dp0 = Math.max(node.virtualFather.dp0, node.dp0);
                node.virtualFather.dp1 = Math.max(node.virtualFather.dp1, node.dp1);
            }

            ans = Math.max(ans, dac(root));
            ans = Math.max(ans, dac(lower));

            return ans;
        }


        public static void collect(Node root, List<Node> trace) {
            if (root.id != -1) {
                trace.add(root);
            }
            for (Edge e : root.edgeList) {
                Node node = e.other(root);
                collect(node, trace);
            }
        }

        public static Edge findCenter(Node root, int upBound) {
            for (Edge e : root.edgeList) {
                Node node = e.other(root);
                int m = Math.max(root.size - node.size,
                        node.size);
                if (m <= upBound) {
                    return e;
                }
                Edge c = findCenter(node, upBound);
                if (c != null) {
                    return c;
                }
            }
            return null;
        }

        public static void calcSize(Node root) {
            root.size = 1;
            for (Edge e : root.edgeList) {
                Node node = e.other(root);
                calcSize(node);
                root.size += node.size;
            }
        }

        public void dfs(Node root, Edge from, long prefix, List<Node> list, boolean expand,
                        int depth) {
            root.edgeList.remove(from);
            root.depth = depth;
            if (expand) {
                expand(root);
            }

            root.open = list.size();
            list.add(root);

            root.prefix = prefix;
            for (Edge e : root.edgeList) {
                Node node = e.other(root);
                dfs(node, e, prefix + e.len, list, expand, depth + 1);
                list.add(root);
            }
            root.close = list.size() - 1;
        }
    }

    public static class Edge {
        Node a;
        Node b;
        long len;

        public Edge(Node a, Node b, long len) {
            this.a = a;
            this.b = b;
            this.len = len;
        }

        Node other(Node me) {
            return a == me ? b : a;
        }

        void replace(Node x, Node y) {
            if (a == x) {
                a = y;
            }
            if (b == x) {
                b = y;
            }
        }

        Node higher() {
            return a.depth < b.depth ? a : b;
        }

        Node lower() {
            return a.depth < b.depth ? b : a;
        }

        @Override
        public String toString() {
            return a + "->" + b;
        }
    }

    public static class Node {
        List<Edge> edgeList = new ArrayList();
        Node virtualFather;
        long prefix;
        int open;
        int close;
        int size;
        int depth;
        int id = -1;
        long dp0;
        long dp1;

        int time;
        long fixWeight;
        int type;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    /**
     * Created by dalt on 2018/5/20.
     */
    public static class SparseTable<T> {
        //st[i][j] means the min value between [i, i + 2^j),
        //so st[i][j] equals to min(st[i][j - 1], st[i + 2^(j - 1)][j - 1])
        Object[][] st;
        Comparator<T> comparator;

        int[] floorLogTable;

        public SparseTable(Object[] data, int length, Comparator<T> comparator) {
            int m = floorLog2(length);
            st = new Object[m + 1][length];
            this.comparator = comparator;
            for (int i = 0; i < length; i++) {
                st[0][i] = data[i];
            }
            for (int i = 0; i < m; i++) {
                int interval = 1 << i;
                for (int j = 0; j < length; j++) {
                    if (j + interval < length) {
                        st[i + 1][j] = min((T) st[i][j], (T) st[i][j + interval]);
                    } else {
                        st[i + 1][j] = st[i][j];
                    }
                }
            }

            floorLogTable = new int[length + 1];
            int log = 1;
            for (int i = 0; i <= length; i++) {
                if ((1 << log) <= i) {
                    log++;
                }
                floorLogTable[i] = log - 1;
            }
        }

        public static int floorLog2(int x) {
            return 31 - Integer.numberOfLeadingZeros(x);
        }

        private T min(T a, T b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }

        public static int ceilLog2(int x) {
            return 32 - Integer.numberOfLeadingZeros(x - 1);
        }

        /**
         * query the min value in [left,right]
         */
        public T query(int left, int right) {
            int queryLen = right - left + 1;
            int bit = floorLogTable[queryLen];
            //x + 2^bit == right + 1
            //So x should be right + 1 - 2^bit - left=queryLen - 2^bit
            return min((T) st[bit][left], (T) st[bit][right + 1 - (1 << bit)]);
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
