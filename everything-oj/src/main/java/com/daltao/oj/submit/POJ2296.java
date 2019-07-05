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

public class POJ2296 {
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
            int t = io.readInt();
            while (t-- > 0) {
                solve();
            }
        }

        TwoSat sat = new TwoSat(100);
        int[][] pos = new int[2][101];
        int n;

        public void solve() {
            n = io.readInt();
            //true mean lower, false mean up
            for (int i = 1; i <= n; i++) {
                pos[0][i] = io.readInt();
                pos[1][i] = io.readInt();
            }

            int l = 0;
            int r = 10000;
            while (l < r) {
                int m = (l + r + 1) >> 1;
                if (possible(m)) {
                    l = m;
                } else {
                    r = m - 1;
                }
            }

            io.cache.append(l).append('\n');
        }

        public boolean possible(int size) {
            sat.reset(n);
            for (int i = 1; i <= n; i++) {
                for (int j = i + 1; j <= n; j++) {
                    int x1 = pos[0][i];
                    int y1 = pos[1][i];
                    int x2 = pos[0][j];
                    int y2 = pos[1][j];
                    int dx = Math.abs(x1 - x2);
                    int dy = Math.abs(y1 - y2);
                    if (dx >= size) {
                        continue;
                    }
                    //tt
                    if (dy < size) {
                        sat.xor(sat.getNotElement(i), sat.getNotElement(j));
                    }
                    if (dy < 2 * size) {
                        if (y1 < y2) {
                            sat.atLeastOneIsFalse(sat.getNotElement(i), sat.getElement(j));
                        } else if (y1 > y2) {
                            sat.atLeastOneIsFalse(sat.getElement(i), sat.getNotElement(j));
                        }
                    }
                }
            }

            return sat.solve(false);
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

    public static class TwoSat {
        public static class Node {
            List<Node> outEdge = new ArrayList(2);
            List<Node> inEdge = new ArrayList(2);
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

        public void and(Node a, Node b) {
            alwaysTrue(a);
            alwaysTrue(b);
        }

        public void or(Node a, Node b) {
            addEdge(a.inverse, b);
            addEdge(b.inverse, a);
        }

        public void atLeastOneIsFalse(Node a, Node b) {
            or(a.inverse, b.inverse);
        }

        public void xor(Node a, Node b) {
            notEqual(a, b);
        }

        public void notEqual(Node a, Node b) {
            same(a, b.inverse);
        }

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
