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

public class CF1215F {
    public static void main(String[] args) throws Exception {
        boolean local = System.getSecurityManager() == null;
        boolean async = true;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("D:\\DATABASE\\TESTCASE\\Code.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io, new Debug(local));

        if (async) {
            Thread t = new Thread(null, task, "skypool", 1 << 27);
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
            int p = io.readInt();
            int M = io.readInt();
            int m = io.readInt();

            TwoSat twoSat = new TwoSat(p + M + 1);

            for (int i = 1; i <= M; i++) {
                twoSat.deduce(twoSat.getElement(p + i + 1), twoSat.getElement(p + i));
            }

            for (int i = 0; i < n; i++) {
                int x = io.readInt();
                int y = io.readInt();
                twoSat.or(twoSat.getElement(x), twoSat.getElement(y));
            }

            for (int i = 1; i <= p; i++) {
                int l = io.readInt();
                int r = io.readInt();
                twoSat.deduce(twoSat.getElement(i), twoSat.getElement(p + l));
                twoSat.deduce(twoSat.getElement(i), twoSat.getNotElement(p + r + 1));
            }

            for (int i = 1; i <= m; i++) {
                int u = io.readInt();
                int v = io.readInt();
                twoSat.atLeastOneIsFalse(twoSat.getElement(u), twoSat.getElement(v));
            }

            if (!twoSat.solve(true)) {
                io.cache.append(-1);
                return;
            }
            int k = 0;
            int f = 0;
            for (int i = 1; i <= p; i++) {
                if (twoSat.valueOf(i)) {
                    k++;
                }
            }

            for (int i = 1; i <= M; i++) {
                if (twoSat.valueOf(p + i) && !twoSat.valueOf(p + i + 1)) {
                    f = i;
                    break;
                }
            }

            io.cache.append(k).append(' ').append(f).append('\n');
            for (int i = 1; i <= p; i++) {
                if (twoSat.valueOf(i)) {
                    io.cache.append(i).append(' ');
                }
            }
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

    public static class TwoSat {
        public static class Node {
            List<Node> outEdge = new ArrayList(0);
            List<Node> inEdge = new ArrayList(0);
            int id;
            Node inverse;
            Node head;
            Node next;
            int dfn;
            int low;
            boolean instack;
            int value;
            int relyOn;

            @Override
            public String toString() {
                return "" + id;
            }
        }

        Node[][] nodes;
        Deque<Node> deque;
        int n;

        public TwoSat(int n) {
            this.n = n;
            deque = new ArrayDeque(2 * n);
            nodes = new Node[2][n + 1];
            for (int i = 0; i < 2; i++) {
                for (int j = 1; j <= n; j++) {
                    nodes[i][j] = new Node();
                    nodes[i][j].id = i == 0 ? -j : j;
                }
            }
            for (int i = 0; i < 2; i++) {
                for (int j = 1; j <= n; j++) {
                    nodes[i][j].inverse = nodes[1 - i][j];
                }
            }
            reset(n);
        }

        void reset(int n) {
            this.n = n;
            order = 0;
            for (int i = 0; i < 2; i++) {
                for (int j = 1; j <= n; j++) {
                    nodes[i][j].dfn = -1;
                    nodes[i][j].outEdge.clear();
                    nodes[i][j].inEdge.clear();
                    nodes[i][j].head = null;
                    nodes[i][j].value = -1;
                    nodes[i][j].next = null;
                    nodes[i][j].relyOn = 0;
                }
            }
        }

        public Node getElement(int i) {
            return nodes[1][i];
        }

        public Node getNotElement(int i) {
            return nodes[0][i];
        }

        private void addEdge(Node a, Node b) {
            a.outEdge.add(b);
            b.inEdge.add(a);
        }

        public void alwaysTrue(Node node) {
            addEdge(node.inverse, node);
        }

        public void alwaysFalse(Node node) {
            addEdge(node, node.inverse);
        }

        /**
         * a && b
         */
        public void and(Node a, Node b) {
            alwaysTrue(a);
            alwaysTrue(b);
        }

        /**
         * a || b
         */
        public void or(Node a, Node b) {
            addEdge(a.inverse, b);
            addEdge(b.inverse, a);
        }

        /**
         * a -> b
         */
        public void deduce(Node a, Node b) {
            or(a.inverse, b);
        }

        /**
         * a == false || b == false
         */
        public void atLeastOneIsFalse(Node a, Node b) {
            or(a.inverse, b.inverse);
        }

        /**
         * a ^ b
         */
        public void xor(Node a, Node b) {
            notEqual(a, b);
        }

        /**
         * a != b
         */
        public void notEqual(Node a, Node b) {
            same(a, b.inverse);
        }

        /**
         * a == b
         */
        public void same(Node a, Node b) {
            addEdge(a, b);
            addEdge(b, a);
            addEdge(a.inverse, b.inverse);
            addEdge(b.inverse, a.inverse);
        }

        public boolean valueOf(int i) {
            return nodes[1][i].value == 1;
        }

        public boolean solve(boolean fetchValue) {
            for (int i = 0; i < 2; i++) {
                for (int j = 1; j <= n; j++) {
                    tarjan(nodes[i][j]);
                }
            }
            for (int i = 1; i <= n; i++) {
                if (nodes[0][i].head == nodes[1][i].head) {
                    return false;
                }
            }

            if (!fetchValue) {
                return true;
            }

            //Topological sort
            for (int i = 0; i < 2; i++) {
                for (int j = 1; j <= n; j++) {
                    for (Node node : nodes[i][j].outEdge) {
                        if (node.head != nodes[i][j].head) {
                            nodes[i][j].head.relyOn++;
                        }
                    }
                }
            }

            for (int i = 0; i < 2; i++) {
                for (int j = 1; j <= n; j++) {
                    if (nodes[i][j].head == nodes[i][j] && nodes[i][j].relyOn == 0) {
                        deque.addLast(nodes[i][j]);
                    }
                }
            }

            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                if (head.inverse.value != -1) {
                    head.value = 0;
                } else {
                    head.value = 1;
                }
                for (Node trace = head; trace != null; trace = trace.next) {
                    trace.value = head.value;
                    for (Node node : trace.inEdge) {
                        if (node.head == head) {
                            continue;
                        }
                        node.head.relyOn--;
                        if (node.head.relyOn == 0) {
                            deque.addLast(node.head);
                        }
                    }
                }
            }

            return true;
        }

        int order;

        private void tarjan(Node root) {
            if (root.dfn >= 0) {
                return;
            }
            root.low = root.dfn = order++;
            deque.addLast(root);
            root.instack = true;
            for (Node node : root.outEdge) {
                tarjan(node);
                if (node.instack) {
                    root.low = Math.min(root.low, node.low);
                }
            }
            if (root.dfn == root.low) {
                while (true) {
                    Node head = deque.removeLast();
                    head.instack = false;
                    head.head = root;
                    if (head == root) {
                        break;
                    }
                    head.next = root.next;
                    root.next = head;
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                builder.append(valueOf(i)).append(' ');
            }
            return builder.toString();
        }
    }
}
