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

public class LUOGU4220 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getSecurityManager() == null;
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

        Node[] tree1;
        Node[] tree2;
        Node[] tree3;
        long[] w;
        long[] fxW;
        int[] color;
        int[] version;
        Node[] virtualFather;
        int[][][] farthest;

        SparseTable<Node> st1;
        SparseTable<Node> st2;
        SparseTable<Node> st3;
        Deque<Node> deque;

        public void solve() {
            int n = io.readInt();

            Node[][] trees = new Node[3][n + 1];
            tree1 = trees[0];
            tree2 = trees[1];
            tree3 = trees[2];
            w = new long[n + 1];
            fxW = new long[n + 1];
            color = new int[n + 1];
            version = new int[n + 1];
            virtualFather = new Node[n + 1];
            farthest = new int[2][n + 1][2];
            deque = new ArrayDeque<>(n);


            for (int i = 0; i < 3; i++) {
                for (int j = 1; j <= n; j++) {
                    trees[i][j] = new Node();
                    trees[i][j].id = j;
                    trees[i][j].edges = new ArrayList<>(2);
                }
            }

            for (int i = 0; i < 3; i++) {
                for (int j = 1; j < n; j++) {
                    Node a = trees[i][io.readInt()];
                    Node b = trees[i][io.readInt()];
                    Edge e = new Edge();
                    e.a = a;
                    e.b = b;
                    e.len = io.readLong();
                    a.edges.add(e);
                    b.edges.add(e);
                }
            }

            for (int i = 0; i < 3; i++) {
                trim(trees[i][1], null);
            }

            buf = new Node[4 * n];
            initBuf();
            dfsForCollectDataToBuildSt(tree1[1]);
            st1 = new SparseTable<>(buf, bufTail, Node.sortByDfn);
            initBuf();
            dfsForCollectDataToBuildSt(tree2[1]);
            st2 = new SparseTable<>(buf, bufTail, Node.sortByDfn);
            initBuf();
            dfsForCollectDataToBuildSt(tree3[1]);
            st3 = new SparseTable<>(buf, bufTail, Node.sortByDfn);

            expand(tree1[1]);
            for (int i = 0; i < 3; i++) {
                dfsForPrefix(trees[i][1], 0);
            }

            for (int i = 0; i < 3; i++) {
                for (int j = 1; j <= n; j++) {
                    w[j] += trees[i][j].prefix;
                }
            }

            dac(tree1[1]);
            io.cache.append(ans);
        }

        int now;

        public void dac(Node a) {
            dfsForSize(a);
            if (a.size == 1) {
                return;
            }
            now++;
            Node b = dfsForDeleteCenter(a, a.size, dfsForCenter(a, a.size));
            initBuf();
            collect(a);
            int sep = bufTail;
            collect(b);
            if (sep == 0) {
                dac(b);
                return;
            }
            if(sep == bufTail){
                dac(a);
                return;
            }
            for (int i = 0; i < sep; i++) {
                int id = buf[i].id;
                fxW[id] = w[id] - 2 * lca(buf[i], buf[sep], st1).prefix;
                color[id] = 1;
                version[id] = now;
                buf[i] = tree2[id];
            }
            for (int i = sep; i < bufTail; i++) {
                int id = buf[i].id;
                fxW[id] = w[id];
                color[id] = 2;
                version[id] = now;
                buf[i] = tree2[id];
            }
            Arrays.sort(buf, 0, bufTail, Node.sortByDfn);
            for (int i = 1, until = bufTail; i < until; i++) {
                Node lca = lca(buf[i], buf[i - 1], st2);
                if (version[lca.id] != now) {
                    version[lca.id] = now;
                    color[lca.id] = 0;
                    buf[bufTail++] = lca;
                }
            }
            Arrays.sort(buf, 0, bufTail, Node.sortByDfn);
            deque.clear();
            for (int i = 0; i < bufTail; i++) {
                while (!deque.isEmpty()) {
                    Node tail = deque.removeLast();
                    if (lca(tail, buf[i], st2) == tail) {
                        deque.addLast(tail);
                        break;
                    }
                }
                if (!deque.isEmpty()) {
                    virtualFather[buf[i].id] = deque.peekLast();
                } else {
                    virtualFather[buf[i].id] = null;
                }
                deque.addLast(buf[i]);
            }

            for (int i = 0; i < bufTail; i++) {
                Node node = buf[i];
                int id = node.id;
                farthest[0][id][0] = farthest[0][id][1]
                        = farthest[1][id][0] = farthest[1][id][1] = 0;
                if (color[id] == 1) {
                    farthest[0][id][0] = farthest[0][id][1] = id;
                }
                if (color[id] == 2) {
                    farthest[1][id][0] = farthest[1][id][1] = id;
                }
            }

            for (int i = bufTail - 1; i >= 0; i--) {
                Node node = buf[i];
                if (virtualFather[node.id] == null) {
                    continue;
                }
                merge(virtualFather[node.id].id, node.id, virtualFather[node.id].id);
            }

            dac(a);
            dac(b);
        }

        long ans = 0;

        public void merge(int fa, int a, int lca) {
            for (int k = 0; k < 2; k++) {
                if (farthest[k][fa][0] != 0 &&
                        farthest[1 - k][a][0] != 0) {
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            int x = farthest[k][fa][i];
                            int y = farthest[1 - k][a][j];
                            ans = Math.max(ans, dist(x, y) - 2 * tree2[lca].prefix);
                        }
                    }
                }
            }

            for (int k = 0; k < 2; k++) {
                if (farthest[k][fa][0] == 0) {
                    farthest[k][fa][0] = farthest[k][a][0];
                    farthest[k][fa][1] = farthest[k][a][1];
                }
                if (farthest[k][a][0] != 0) {
                    int x = farthest[k][fa][0];
                    int y = farthest[k][fa][1];
                    if (dist(farthest[k][a][0], farthest[k][a][1])
                            > dist(x, y)) {
                        x = farthest[k][a][0];
                        y = farthest[k][a][1];
                    }
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            if (dist(farthest[k][fa][i], farthest[k][a][j])
                                    > dist(x, y)) {
                                x = farthest[k][fa][i];
                                y = farthest[k][a][j];
                            }
                        }
                    }
                    farthest[k][fa][0] = x;
                    farthest[k][fa][1] = y;
                }
            }
        }

        public long dist(int x, int y) {
            return fxW[x] + fxW[y] - 2 * lca(tree3[x], tree3[y], st3).prefix;
        }

        public Node lca(Node a, Node b, SparseTable<Node> st) {
            return st.query(Math.min(a.dfn, b.dfn), Math.max(a.dfn, b.dfn));
        }

        public void collect(Node root) {
            if (root.id != -1) {
                buf[bufTail++] = root;
            }
            for (Edge e : root.edges) {
                collect(e.other(root));
            }
        }

        public void dfsForSize(Node root) {
            root.size = 1;
            for (Edge e : root.edges) {
                Node node = e.other(root);
                dfsForSize(node);
                root.size += node.size;
            }
        }

        public int dfsForCenter(Node root, int total) {
            int match = Math.max(total - root.size, root.size);
            for (Edge e : root.edges) {
                Node node = e.other(root);
                match = Math.min(match, dfsForCenter(node, total));
            }
            return match;
        }

        public Node dfsForDeleteCenter(Node root, int total, int key) {
            int match = Math.max(total - root.size, root.size);
            if (match == key) {
                return root;
            }
            for (Edge e : root.edges) {
                Node node = e.other(root);
                Node ret = dfsForDeleteCenter(node, total, key);
                if (ret == node) {
                    root.edges.remove(e);
                }
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }


        Node[] buf;
        int bufTail;

        public void initBuf() {
            bufTail = 0;
        }

        public void dfsForCollectDataToBuildSt(Node root) {
            root.dfn = bufTail;
            buf[bufTail++] = root;
            for (Edge e : root.edges) {
                dfsForCollectDataToBuildSt(e.other(root));
                buf[bufTail++] = root;
            }
        }

        public void dfsForPrefix(Node root, long prefix) {
            root.prefix = prefix;
            for (Edge e : root.edges) {
                dfsForPrefix(e.other(root), prefix + e.len);
            }
        }

        public void trim(Node root, Edge fa) {
            root.edges.remove(fa);
            for (Edge e : root.edges) {
                trim(e.other(root), e);
            }
        }

        public void expand(Node root) {
            if (root.edges.size() > 2) {
                Node trace = root;
                while (trace.edges.size() > 2) {
                    Node virtual = new Node();
                    virtual.edges = trace.edges;
                    trace.edges = new ArrayList<>(2);
                    trace.edges.add(virtual.edges.remove(virtual.edges.size() - 1));
                    trace.edges.add(Edge.of(trace, virtual, 0));
                    trace.edges.get(0).replace(root, trace);
                    trace = virtual;
                }
                for (Edge e : trace.edges) {
                    e.replace(root, trace);
                }
            }

            for (Edge e : root.edges) {
                expand(e.other(root));
            }
        }
    }

    public static class Edge {
        Node a;
        Node b;
        long len;

        public static Edge of(Node a, Node b, long len) {
            Edge e = new Edge();
            e.a = a;
            e.b = b;
            e.len = len;
            return e;
        }

        Node other(Node x) {
            return a == x ? b : a;
        }

        void replace(Node x, Node y) {
            if (x == b) {
                b = y;
            }
            if (x == a) {
                a = y;
            }
        }

        @Override
        public String toString() {
            return String.format("(%s,%s)", a, b);
        }
    }

    public static class Node {
        List<Edge> edges;
        int id = -1;
        int size;
        long prefix;
        int dfn;
        static Comparator<Node> sortByDfn = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.dfn - o2.dfn;
            }
        };

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
