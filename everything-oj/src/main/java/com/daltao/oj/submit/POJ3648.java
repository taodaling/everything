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

public class POJ3648 {
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
        static int limit = 50;
        Node[][] nodes = new Node[limit][2];
        int n;
        int m;
        Deque<Node> deque = new ArrayDeque(limit);

        {
            for (int i = 0; i < limit; i++) {
                nodes[i][0] = new Node();
                nodes[i][1] = new Node();
                nodes[i][0].id = i;
                nodes[i][1].id = -i;
            }
        }

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            while (true) {
                n = io.readInt();
                m = io.readInt();
                if (n == 0 && m == 0) {
                    break;
                }
                solveSingleCase();
            }

        }


        public void solveSingleCase() {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    nodes[i][j].dfn = 0;
                    nodes[i][j].out.clear();
                    nodes[i][j].in.clear();
                    nodes[i][j].relyOn = 0;
                    nodes[i][j].val = 0;
                    nodes[i][j].opposite = nodes[i][1 - j];
                }
            }
            addEdge(nodes[0][1], nodes[0][0]);
            for (int i = 0; i < m; i++) {
                //!(a&&b)
                Node a = nodes[io.readInt()][readGender()];
                Node b = nodes[io.readInt()][readGender()];
                addEdge(a, b.opposite);
                addEdge(b, a.opposite);
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    tarjan(nodes[i][j], deque);
                }
                if (nodes[i][0].set == nodes[i][1].set) {
                    io.cache.append("bad luck\n");
                    return;
                }
            }


            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    if (nodes[i][j] != nodes[i][j].set) {
                        continue;
                    }
                    for (Node node : nodes[i][j].in) {
                        node = node.set;
                        if (node == nodes[i][j]) {
                            continue;
                        }
                        node.relyOn++;
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    if (nodes[i][j] != nodes[i][j].set) {
                        continue;
                    }
                    if (nodes[i][j].relyOn == 0) {
                        deque.addLast(nodes[i][j]);
                    }
                }
            }

            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                for (Node node : head.in) {
                    node = node.set;
                    if (node == head) {
                        continue;
                    }
                    node.relyOn--;
                    if (node.relyOn == 0) {
                        deque.addLast(node);
                    }
                }
                head.val = -head.opposite.set.val;
                if (head.val == 0) {
                    head.val = 1;
                }
            }

            for (int i = 1; i < n; i++) {
                if (nodes[i][0].set.val == 1) {
                    io.cache.append(i).append('w');
                } else {
                    io.cache.append(i).append('h');
                }
                io.cache.append(' ');
            }
            io.cache.append('\n');
        }

        public int readGender() {
            return io.readChar() == 'h' ? 0 : 1;
        }

        public void addEdge(Node a, Node b) {
            a.out.add(b);
            b.in.add(a);
        }

        static int id = 0;

        public static int order() {
            return ++id;
        }

        public static void tarjan(Node root, Deque<Node> deque) {
            if (root.dfn != 0) {
                return;
            }
            root.low = root.dfn = order();
            root.inStack = true;
            deque.addLast(root);
            for (Node node : root.out) {
                tarjan(node, deque);
                if (node.inStack) {
                    root.low = Math.min(root.low, node.low);
                }
            }
            if (root.low == root.dfn) {
                while (true) {
                    Node last = deque.removeLast();
                    last.inStack = false;
                    last.set = root;
                    if (last == root) {
                        break;
                    }
                    root.out.addAll(last.out);
                    root.in.addAll(last.in);
                }
            }
        }
    }

    public static class Node {
        List<Node> out = new ArrayList();
        List<Node> in = new ArrayList();
        int id;
        int dfn;
        int low;
        Node opposite;
        boolean inStack;
        Node set;
        int relyOn;
        int val;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class FastIO {
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        public final StringBuilder cache = new StringBuilder();

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
                sign = next == '+' ? true : false;
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

        public char readChar() {
            skipBlank();
            char c = (char) next;
            next = read();
            return c;
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

        public Debug(boolean allowDebug) {
            this.allowDebug = allowDebug;
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
