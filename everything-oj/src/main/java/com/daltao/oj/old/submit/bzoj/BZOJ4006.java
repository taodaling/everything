package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BZOJ4006 {
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
        Node[] nodes;
        int idAllocator = 0;
        int[] channels;
        SubsetGenerator generator = new SubsetGenerator();
        Deque<Node> deque;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        public void squeeze() {
            int wpos = 0;
            int rpos = 0;
            while (rpos < channels.length) {
                if (channels[rpos] != 0) {
                    channels[wpos++] = channels[rpos];
                }
                rpos++;
            }
            channels = Arrays.copyOfRange(channels, 0, wpos);
        }

        void spfa(int mask) {
            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                head.inque = false;
                for (Edge edge : head.edgeList) {
                    Node node = edge.another(head);
                    int cost = head.dp[mask] + edge.cost;
                    if (cost >= node.dp[mask | node.bit]) {
                        continue;
                    }
                    node.dp[mask | node.bit] = cost;
                    if ((mask | node.bit) != mask || node.inque) {
                        continue;
                    }
                    node.inque = true;
                    deque.addLast(node);
                }
            }
        }

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();
            int p = io.readInt();
            channels = new int[p];
            deque = new ArrayDeque(n);
            nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
                nodes[i].dp = new int[1 << p];
                Arrays.fill(nodes[i].dp, inf);
                nodes[i].dp[0] = 0;
            }
            for (int i = 1; i <= m; i++) {
                Edge edge = new Edge();
                edge.a = nodes[io.readInt()];
                edge.b = nodes[io.readInt()];
                edge.cost = io.readInt();
                edge.a.edgeList.add(edge);
                edge.b.edgeList.add(edge);
            }
            for (int i = 0; i < p; i++) {
                int c = io.readInt();
                Node d = nodes[io.readInt()];
                d.bit = 1 << (idAllocator++);
                d.dp[d.bit] = 0;
                d.dp[0] = inf;
                channels[c - 1] |= d.bit;
            }
            int mask = (1 << idAllocator) - 1;
            for (int i = 0; i <= mask; i++) {
                for (int j = 1; j <= n; j++) {
                    Node node = nodes[j];
                    if ((i & node.bit) != node.bit) {
                        continue;
                    }
                    generator.setSet(i);
                    while (generator.hasNext()) {
                        int s = generator.next();
                        int s1 = node.bit | s;
                        int s2 = node.bit | (i - s);
                        if (s1 == node.bit || s2 == node.bit) {
                            continue;
                        }
                        node.dp[i] = Math.min(node.dp[i], node.dp[s1] + node.dp[s2]);
                    }
                    if (node.dp[i] < inf) {
                        deque.addLast(node);
                        node.inque = true;
                    }
                }
                spfa(i);
            }
            //squeeze();
            int[] channel2IdBits = new int[1 << channels.length];
            int[] minDp = new int[1 << idAllocator];
            Arrays.fill(minDp, inf);
            for (int i = 1; i <= n; i++) {
                for (int j = 0; j <= mask; j++) {
                    minDp[j] = Math.min(minDp[j], nodes[i].dp[j]);
                }
            }
            int[] dp = new int[1 << channels.length];
            Arrays.fill(dp, inf);
            dp[0] = 0;
            for (int i = 1, until = channel2IdBits.length; i < until; i++) {
                if (i == (i & -i)) {
                    channel2IdBits[i] = channels[31 - Integer.numberOfLeadingZeros(i)];
                } else {
                    channel2IdBits[i] = channel2IdBits[i & (i - 1)] + channel2IdBits[i & (-i)];
                }
            }
            for (int i = 1, until = 1 << channels.length; i < until; i++) {
                generator.setSet(i);
                while (generator.hasNext()) {
                    int s = generator.next();
                    int s2 = i - s;
                    dp[i] = Math.min(dp[i], dp[s] + minDp[channel2IdBits[s2]]);
                }
            }
            io.cache.append(dp[(1 << channels.length) - 1]);
        }
    }

    public static class Edge {
        Node a;
        Node b;a
        int cost;

        public Node another(Node me) {
            return a == me ? b : a;
        }
    }

    public static class Node {
        int bit;
        int id;
        List<Edge> edgeList = new ArrayList();
        int[] dp;
        boolean inque;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class SubsetGenerator {
        private int[] meanings = new int[33];
        private int[] bits = new int[33];
        private int remain;
        private int next;

        public void setSet(int set) {
            int bitCount = 0;
            while (set != 0) {
                meanings[bitCount] = set & -set;
                bits[bitCount] = 0;
                set -= meanings[bitCount];
                bitCount++;
            }
            remain = 1 << bitCount;
            next = 0;
        }

        public boolean hasNext() {
            return remain > 0;
        }

        private void consume() {
            remain = remain - 1;
            int i;
            for (i = 0; bits[i] == 1; i++) {
                bits[i] = 0;
                next -= meanings[i];
            }
            bits[i] = 1;
            next += meanings[i];
        }

        public int next() {
            int returned = next;
            consume();
            return returned;
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
