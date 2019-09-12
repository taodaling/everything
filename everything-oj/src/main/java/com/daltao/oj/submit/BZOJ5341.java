package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BZOJ5341 {
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

        Node[] tree1;
        Node[] tree2;

        public void solve() {
            int n = io.readInt();
            tree1 = new Node[n + 1];
            tree2 = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                tree1[i] = new Node();
                tree2[i] = new Node();
                tree1[i].edges = new ArrayList();
                tree2[i].edges = new ArrayList();
                tree1[i].id = i;
                tree2[i].id = i;
                tree2[i].head = tree1[i].head = new EdgeNode();
                tree1[i].head.w = -lInf;
                tree1[i].self = tree1[i];
            }
            buildTree(tree1, n);
            buildTree(tree2, n);
            dfs1(tree1[1], null, 0);
            dfs1(tree2[1], null, 0);
            expand(tree1[1]);
            dac(tree1[1]);
            dfsForDp(tree2[1], null);
            io.cache.append(ans);
        }

        long ans = -lInf;

        public void dfsForDp(Node root, Edge from) {
            if (root == Node.NIL) {
                return;
            }
            for (Edge e : root.edges) {
                Node node = e.other(root);
                dfsForDp(node, e);
                root.head = merge(root.head, node.head, root);
            }
        }

        public EdgeNode merge(EdgeNode a, EdgeNode b, Node lca) {
            if (a == EdgeNode.NIL) {
                return b;
            }
            if (b == EdgeNode.NIL) {
                return a;
            }
            ans = Math.max(ans, a.l.w + b.r.w - lca.prefix);
            ans = Math.max(ans, b.l.w + a.r.w - lca.prefix);
            a.l = merge(a.l, b.l, lca);
            a.r = merge(a.r, b.r, lca);
            a.w = Math.max(a.w, b.w);
            return a;
        }

        public void dac(Node a) {
            dfsForSize(a);
            if (a.size == 1) {
                return;
            }
            Node b = dfsForCenter2(a, dfsForCenter(a, a.size), a.size);
            dfsSetHigher(a, a);
            dfsSetLower(b);
            dac(a);
            dac(b);
        }

        public void dfsSetLower(Node root) {
            if (root == Node.NIL) {
                return;
            }
            dfsSetLower(root.l);
            dfsSetLower(root.r);
            if (root.id != -1) {
                root.head.r = new EdgeNode();
                root.head = root.head.r;
                root.head.w = root.prefix;
            }
        }

        public void dfsSetHigher(Node root, Node lca) {
            if (root == Node.NIL) {
                return;
            }
            if (root.color) {
                lca = root;
            }
            dfsSetHigher(root.l, lca);
            dfsSetHigher(root.r, lca);
            if (root.id != -1) {
                root.head.l = new EdgeNode();
                root.head = root.head.l;
                root.head.w = root.prefix - lca.prefix;
            }
        }


        public int dfsForCenter(Node root, int total) {
            if (root == Node.NIL) {
                return total;
            }
            int match = Math.max(root.size, total - root.size);
            match = Math.min(dfsForCenter(root.l, total), match);
            match = Math.min(dfsForCenter(root.r, total), match);
            return match;
        }

        public Node dfsForCenter2(Node root, int match, int total) {
            if (root == Node.NIL) {
                return Node.NIL;
            }
            if (Math.max(root.size, total - root.size) == match) {
                return root;
            }
            Node node = dfsForCenter2(root.l, match, total);
            if (node == Node.NIL) {
                node = dfsForCenter2(root.r, match, total);
            }
            if (node != Node.NIL) {
                root.color = true;
                if (root.l == node) {
                    root.l = Node.NIL;
                }
                if (root.r == node) {
                    root.r = Node.NIL;
                }
            }
            return node;
        }

        public void dfs1(Node root, Edge fa, long prefix) {
            root.edges.remove(fa);
            root.prefix = prefix;
            for (Edge e : root.edges) {
                dfs1(e.other(root), e, prefix + e.len);
            }
        }

        public void dfsForSize(Node root) {
            if (root == Node.NIL) {
                return;
            }
            root.color = false;
            dfsForSize(root.l);
            dfsForSize(root.r);
            root.size = root.l.size + root.r.size + 1;
        }

        public void expand(Node root) {
            if (root == Node.NIL) {
                return;
            }
            if (root.edges.size() > 2) {
                Node virtual = new Node();
                virtual.id = -1;
                virtual.self = root.self;
                virtual.prefix = root.prefix;
                virtual.edges = root.edges;
                root.l = root.edges.remove(root.edges.size() - 1).other(root.self);
                root.r = virtual;
            } else {
                if (root.edges.size() > 0) {
                    root.l = root.edges.get(0).other(root.self);
                }
                if (root.edges.size() > 1) {
                    root.r = root.edges.get(1).other(root.self);
                }
            }
            expand(root.l);
            expand(root.r);
        }

        public void buildTree(Node[] tree, int n) {
            for (int i = 2; i <= n; i++) {
                Node a = tree[io.readInt()];
                Node b = tree[io.readInt()];
                Edge e = new Edge();
                e.a = a;
                e.b = b;
                e.len = io.readInt();
                a.edges.add(e);
                b.edges.add(e);
            }
        }
    }

    public static class Edge {
        Node a;
        Node b;
        int len;

        Node other(Node x) {
            return a == x ? b : a;
        }
    }

    public static class Node {
        private static final Node NIL = new Node();

        static {
            NIL.l = NIL.r = NIL;
        }

        List<Edge> edges;
        int size;
        int id;
        Node l = NIL;
        Node r = NIL;
        long prefix;
        Node self;

        EdgeNode head = EdgeNode.NIL;

        boolean color;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class EdgeNode {
        static final EdgeNode NIL = new EdgeNode();

        static {
            NIL.l = NIL;
            NIL.r = NIL;
        }

        EdgeNode l = NIL;
        EdgeNode r = NIL;
        long w = -(long) 1e18;
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
