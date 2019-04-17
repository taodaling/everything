package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class CF1119F {
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
        long lInf = (long) 1e12;

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
            Node[] nodes = new Node[n + 1];
            for (int i = 0; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }

            long totalWeight = 0;
            for (int i = 1; i < n; i++) {
                Node a = nodes[io.readInt()];
                Node b = nodes[io.readInt()];
                Long c = (long) io.readInt();
                a.edgeSet.put(b, c);
                b.edgeSet.put(a, c);
                totalWeight += c;
            }

            Arrays.sort(nodes, 1, n + 1, (a, b) -> a.edgeSet.size() == b.edgeSet.size() ? a.id - b.id : b.edgeSet.size() - a.edgeSet.size());

            dfs(nodes[1], null);
            Segment root = Segment.build(1, n);
            Segment.update(nodes[1].enter, nodes[1].leave, 1, n, nodes[1], root);

            TreeSet<Node> degreeSet = new TreeSet<>((a, b) -> a.degree == b.degree ? a.id - b.id : a.degree - b.degree);
            nodes[1].degree = nodes[1].edgeSet.size();
            for (int i = 2; i <= n; i++) {
                nodes[i].degree = nodes[i].edgeSet.size() - 1;
                degreeSet.add(nodes[i]);
            }

            long[] minCost = new long[n];
            minCost[0] = totalWeight;
            Node last = degreeSet.pollLast();
            for (int i = nodes[1].degree - 1; i >= 1; i--) {
                while (last != null && last.degree >= i) {
                    Node la = Segment.query(last.enter, last.enter, 1, n, root);
                    //is direct father
                    while (true) {
                        Node node = la.virtualEdges.ceilingKey(last);
                        if (node == null || node.leave > last.leave) {
                            break;
                        }
                        la.virtualEdges.remove(node);
                        last.virtualEdges.put(node, last.edgeSet.getOrDefault(node, lInf));
                        if (last.edgeSet.containsKey(node)) {
                            int fee = last.edgeSet.get(node).intValue();
                            last.remainEdges = Treap.remove(last.remainEdges, fee);
                            node.remainEdges = Treap.remove(node.remainEdges, fee);
                        }
                    }
                    la.virtualEdges.put(last, la.edgeSet.getOrDefault(last, lInf));
                    if (la.edgeSet.containsKey(last)) {
                        int fee = la.edgeSet.get(last).intValue();
                        la.remainEdges = Treap.remove(la.remainEdges, fee);
                        last.remainEdges = Treap.remove(last.remainEdges, fee);
                    }

                    Segment.update(last.enter, last.leave, 1, n, last, root);
                    last = degreeSet.pollLast();
                }

                dp(nodes[1], i);
                minCost[i] = nodes[1].dp0;
            }

            for (int i = 0; i < n; i++) {
                io.cache.append(minCost[i]).append(' ');
            }
        }


        public static void dp(Node root, int degreeLimit) {
            long total = 0;
            int degree = root.degree;
            for (Map.Entry<Node, Long> entry : root.virtualEdges.entrySet()) {
                Node node = entry.getKey();
                dp(node, degreeLimit);
                node.fixedFee = entry.getValue() + (node.dp0 - node.dp1);
                total += node.dp1;
            }

            for (Node node : root.virtualEdges.keySet()) {
                if (node.fixedFee < 0) {
                    total += node.fixedFee;
                    degree--;
                } else {
                    root.remainEdges = Treap.insert(root.remainEdges, node.fixedFee);
                }
            }

            if (degree > degreeLimit) {
                Treap[] p = Treap.splitByRank(root.remainEdges, degree - degreeLimit);
                root.dp0 = total + p[0].sum;
                root.remainEdges = Treap.merge(p[0], p[1]);
            } else {
                root.dp0 = total;
            }

            if (degree > degreeLimit - 1) {
                Treap[] p = Treap.splitByRank(root.remainEdges, degree - degreeLimit + 1);
                root.dp1 = total + p[0].sum;
                root.remainEdges = Treap.merge(p[0], p[1]);
            } else {
                root.dp1 = root.dp0;
            }

            for (Node node : root.virtualEdges.keySet()) {
                if (node.fixedFee < 0) {
                } else {
                    root.remainEdges = Treap.remove(root.remainEdges, node.fixedFee);
                }
            }
        }

        void dfs(Node root, Node father) {
            root.enter = order();
            for (Map.Entry<Node, Long> entry : root.edgeSet.entrySet()) {
                Node node = entry.getKey();
                root.remainEdges = Treap.insert(root.remainEdges, entry.getValue());
                if (node == father) {
                    continue;
                }
                dfs(node, root);
            }
            root.leave = order;
        }

        int order = 0;

        public int order() {
            return ++order;
        }
    }

    public static class Treap implements Cloneable {
        private static Random random = new Random();

        private static Treap NIL = new Treap(0);

        static {
            NIL.left = NIL.right = NIL;
            NIL.size = 0;
        }

        Treap left = NIL;
        Treap right = NIL;
        int size;
        long key;
        long sum;

        public static Treap remove(Treap treap, long key) {
            Treap[] p = splitByKey(treap, key - 1);
            Treap[] r = splitByRank(p[1], 1);
            if (r[0].key != key) {
                r[1] = merge(r[0], r[1]);
            }
            return merge(p[0], r[1]);
        }

        public Treap(long key) {
            this.key = this.sum = key;
            this.size = 1;
        }


        @Override
        public Treap clone() {
            try {
                return (Treap) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public void pushDown() {
        }

        public void pushUp() {
            size = left.size + right.size + 1;
            sum = left.sum + right.sum + key;
        }

        public static Treap[] splitByRank(Treap root, int rank) {
            if (root == NIL) {
                return new Treap[]{NIL, NIL};
            }
            root.pushDown();
            Treap[] result;
            if (root.left.size >= rank) {
                result = splitByRank(root.left, rank);
                root.left = result[1];
                result[1] = root;
            } else {
                result = splitByRank(root.right, rank - (root.size - root.right.size));
                root.right = result[0];
                result[0] = root;
            }
            root.pushUp();
            return result;
        }

        public static Treap merge(Treap a, Treap b) {
            if (a == NIL) {
                return b;
            }
            if (b == NIL) {
                return a;
            }
            if (random.nextBoolean()) {
                a.pushDown();
                a.right = merge(a.right, b);
                a.pushUp();
                return a;
            } else {
                b.pushDown();
                b.left = merge(a, b.left);
                b.pushUp();
                return b;
            }
        }

        public static Treap insert(Treap a, long k) {
            Treap node = new Treap(k);
            Treap[] p = splitByKey(a, k);
            p[0] = Treap.merge(p[0], node);
            return Treap.merge(p[0], p[1]);
        }

        public static void toString(Treap root, StringBuilder builder) {
            if (root == NIL) {
                return;
            }
            root.pushDown();
            toString(root.left, builder);
            builder.append(root.key).append(',');
            toString(root.right, builder);
        }

        public static Treap clone(Treap root) {
            if (root == NIL) {
                return NIL;
            }
            Treap clone = root.clone();
            clone.left = clone(root.left);
            clone.right = clone(root.right);
            return clone;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder().append(key).append(":");
            toString(clone(this), builder);
            return builder.toString();
        }

        public static Treap[] splitByKey(Treap root, long key) {
            if (root == NIL) {
                return new Treap[]{NIL, NIL};
            }
            root.pushDown();
            Treap[] result;
            if (root.key > key) {
                result = splitByKey(root.left, key);
                root.left = result[1];
                result[1] = root;
            } else {
                result = splitByKey(root.right, key);
                root.right = result[0];
                result[0] = root;
            }
            root.pushUp();
            return result;
        }


    }


    public static class Node {
        static Comparator<Node> compareById = (a, b) -> a.id - b.id;
        static Comparator<Node> compareByEnter = (a, b) -> a.enter - b.enter;
        TreeMap<Node, Long> virtualEdges = new TreeMap<>(compareByEnter);
        TreeMap<Node, Long> edgeSet = new TreeMap<>(compareById);
        Treap remainEdges = Treap.NIL;

        int id;
        long dp0;
        long dp1;
        int enter;
        int leave;
        int degree;
        long fixedFee;


        @Override
        public String toString() {
            return "" + id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
    }

    public static class Segment implements Cloneable {
        static Node zero = new Node();

        {
            zero.enter = -1;
        }

        Segment left;
        Segment right;
        Node node = zero;
        Node dirty;


        public static Segment build(int l, int r) {
            Segment segment = new Segment();
            if (l != r) {
                int m = (l + r) >> 1;
                segment.left = build(l, m);
                segment.right = build(m + 1, r);
                segment.pushUp();
            }
            return segment;
        }

        public void setDirty(Node dirty) {
            if (this.dirty != null && this.dirty.enter >= dirty.enter) {
                return;
            }
            this.dirty = dirty;
            if (dirty.enter > node.enter) {
                node = dirty;
            }
        }

        public static boolean checkOutOfRange(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public static boolean checkCoverage(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        public static void update(int ll, int rr, int l, int r, Node dirty, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return;
            }
            if (checkCoverage(ll, rr, l, r)) {
                segment.setDirty(dirty);
                return;
            }
            int m = (l + r) >> 1;

            segment.pushDown();
            update(ll, rr, l, m, dirty, segment.left);
            update(ll, rr, m + 1, r, dirty, segment.right);
            segment.pushUp();
        }

        public static Node nodeWithMaxEnter(Node a, Node b) {
            return a.enter >= b.enter ? a : b;
        }

        public static Node query(int ll, int rr, int l, int r, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return zero;
            }
            if (checkCoverage(ll, rr, l, r)) {
                return segment.node;
            }
            int m = (l + r) >> 1;

            segment.pushDown();
            return nodeWithMaxEnter(query(ll, rr, l, m, segment.left),
                    query(ll, rr, m + 1, r, segment.right));
        }

        public void pushDown() {
            if (dirty != null) {
                left.setDirty(dirty);
                right.setDirty(dirty);
                dirty = null;
            }
        }

        public void pushUp() {
            node = nodeWithMaxEnter(left.node, right.node);
        }

    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder();
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 23);
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
