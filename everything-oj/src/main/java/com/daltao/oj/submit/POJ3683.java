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

public class POJ3683 {
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
            Node[] nodes = new Node[n * 2];
            for (int i = 0; i < n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
                nodes[i + n] = new Node();
                nodes[i + n].id = i + n;
                nodes[i].opposite = nodes[i + n];
                nodes[i + n].opposite = nodes[i];

                int b = readTime();
                int e = readTime();
                int c = io.readInt();
                nodes[i].begin = b;
                nodes[i].end = b + c;
                nodes[i + n].begin = e - c;
                nodes[i + n].end = e;
            }

            for (int i = 0, until = nodes.length; i < until; i++) {
                for (int j = 0; j < i; j++) {
                    if (!intersect(nodes[i].begin, nodes[i].end, nodes[j].begin, nodes[j].end)) {
                        continue;
                    }
                    nodes[i].out.add(nodes[j].opposite);
                    nodes[j].opposite.in.add(nodes[i]);
                    nodes[j].out.add(nodes[i].opposite);
                    nodes[i].opposite.in.add(nodes[j]);
                }
            }

            Deque<Node> deque = new ArrayDeque(n);
            for (Node node : nodes) {
                tarjan(node, deque);
            }

            for (Node node : nodes) {
                if (node.set == node.opposite.set) {
                    io.cache.append("NO\n");
                    return;
                }
            }


            for (Node node : nodes) {
                if (node.set != node) {
                    continue;
                }
                for (Node near : node.in) {
                    near = near.set;
                    if (near == node) {
                        continue;
                    }
                    near.relyOn++;
                }
            }

            for (Node node : nodes) {
                if (node.set != node) {
                    continue;
                }
                if (node.relyOn == 0) {
                    deque.addLast(node);
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
                if (head.val != 0) {
                    continue;
                }
                head.val = 1;
                head.opposite.val = -1;
            }

            io.cache.append("YES\n");
            for (int i = 0; i < n; i++) {
                Node node = nodes[i];
                if (node.set.val == -1) {
                    node = node.opposite;
                }
                output(node.begin);
                io.cache.append(' ');
                output(node.end);
                io.cache.append('\n');
            }
        }

        public void output(int time) {
            format(time / 60);
            io.cache.append(':');
            format(time % 60);
        }

        public void format(int num) {
            if (num < 10) {
                io.cache.append('0');
            }
            io.cache.append(num);
        }

        public static boolean intersect(int b1, int e1, int b2, int e2) {
            return !((e1 <= b2) || (b1 >= e2));
        }

        char[] buf = new char[5];

        public int readTime() {
            io.readString(buf, 0);
            int hour = (buf[0] - '0') * 10 + buf[1] - '0';
            int minutes = (buf[3] - '0') * 10 + buf[4] - '0';
            return hour * 60 + minutes;
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
        int begin;
        int end;
        boolean inStack;
        Node set;
        Node opposite;
        int val = 0;
        int relyOn;

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
