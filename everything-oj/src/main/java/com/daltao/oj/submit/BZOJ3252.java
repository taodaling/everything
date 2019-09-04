package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

public class BZOJ3252 {
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

        public void solve() {
            int n = io.readInt();
            int k = io.readInt();
            Node[] nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].weight = io.readInt();
            }
            for (int i = 1; i < n; i++) {
                Node a = nodes[io.readInt()];
                Node b = nodes[io.readInt()];
                a.next.add(b);
                b.next.add(a);
            }
            nodes[0] = new Node();
            nodes[0].next.add(nodes[1]);
            dfs(nodes[0], null);

            long total = 0;
            for (int i = 0; i < k; i++) {
                Node heaviest = nodes[0].lst.peek();
                nodes[0].lst = LeftSideTree.pop(nodes[0].lst, Node.sortByHeaviest);
                total += pop(heaviest, nodes[0]);
            }

            io.cache.append(total);
        }

        public void dfs(Node root, Node father) {
            root.father = father;
            for (Node node : root.next) {
                if (node == father) {
                    continue;
                }
                dfs(node, root);
                root.lst = LeftSideTree.merge(root.lst, new LeftSideTree(node), Node.sortByHeaviest);
            }
            if (root.lst != LeftSideTree.NIL) {
                root.heaviest = root.lst.peek().heaviest + root.weight;
            } else {
                root.heaviest = root.weight;
            }
        }

        public long pop(Node node, Node root) {
            if (node.lst == LeftSideTree.NIL) {
                return node.weight;
            }
            Node heaviest = node.lst.peek();
            node.lst = LeftSideTree.pop(node.lst, Node.sortByHeaviest);
            root.lst = LeftSideTree.merge(node.lst, root.lst, Node.sortByHeaviest);
            return pop(heaviest, root) + node.weight;
        }
    }

    public static class Node {
        static final Comparator<Node> sortByHeaviest = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return -(o1.heaviest < o2.heaviest ? -1 : o1.heaviest > o2.heaviest ? 1 : 0);
            }
        };

        long weight;
        long heaviest;
        LeftSideTree<Node> lst = LeftSideTree.NIL;
        List<Node> next = new ArrayList(2);
        Node father;
    }

    public static class LeftSideTree<K> {
        public static final LeftSideTree NIL = new LeftSideTree(null);

        static {
            NIL.left = NIL;
            NIL.right = NIL;
            NIL.dist = -1;
        }

        LeftSideTree<K> left = NIL;
        LeftSideTree<K> right = NIL;
        int dist;
        K key;

        public LeftSideTree(K key) {
            this.key = key;
        }

        public static <K> LeftSideTree<K> createFromCollection(Collection<LeftSideTree<K>> trees, Comparator<K> cmp) {
            return createFromDeque(new ArrayDeque(trees), cmp);
        }

        public static <K> LeftSideTree<K> createFromDeque(Deque<LeftSideTree<K>> deque, Comparator<K> cmp) {
            while (deque.size() > 1) {
                deque.addLast(merge(deque.removeFirst(), deque.removeFirst(), cmp));
            }
            return deque.removeLast();
        }

        public static <K> LeftSideTree<K> merge(LeftSideTree<K> a, LeftSideTree<K> b, Comparator<K> cmp) {
            if (a == NIL) {
                return b;
            } else if (b == NIL) {
                return a;
            }
            if (cmp.compare(a.key, b.key) > 0) {
                LeftSideTree<K> tmp = a;
                a = b;
                b = tmp;
            }
            a.right = merge(a.right, b, cmp);
            if (a.left.dist < a.right.dist) {
                LeftSideTree<K> tmp = a.left;
                a.left = a.right;
                a.right = tmp;
            }
            a.dist = a.right.dist + 1;
            return a;
        }

        public boolean isEmpty() {
            return this == NIL;
        }

        public K peek() {
            return key;
        }

        public static <K> LeftSideTree<K> pop(LeftSideTree<K> root, Comparator<K> cmp) {
            return merge(root.left, root.right, cmp);
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
