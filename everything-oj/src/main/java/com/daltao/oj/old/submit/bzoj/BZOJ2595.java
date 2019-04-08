package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class BZOJ2595 {
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
        long lInf = (long) 1e18;
        Node[][] nodes;
        int n;
        int m;
        int idAllocator;
        Deque<Node> deque;
        int[][] directions = new int[][]{
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };
        SubsetGenerator generator = new SubsetGenerator();

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        public boolean inScope(int i, int j) {
            return i >= 0 && i < n && j >= 0 && j < m;
        }

        public void inverse(Node root, int mask) {
            root.cover = true;
            if (mask == root.bit) {
                return;
            }
            generator.setSet(mask);
            while (generator.hasNext()) {
                int s = generator.next();
                int s1 = s | root.bit;
                int s2 = (mask - s) | root.bit;
                if (s1 == mask || s2 == mask) {
                    continue;
                }
                if (root.dp[mask] == root.dp[s1] + root.dp[s2] - root.cost) {
                    inverse(root, s1);
                    inverse(root, s2);
                    return;
                }
            }
            for (int[] direction : directions) {
                int r = root.row + direction[0];
                int c = root.col + direction[1];
                if (!inScope(r, c)) {
                    continue;
                }
                Node node = nodes[r][c];
                if (root.dp[mask] == node.dp[mask - root.bit] + root.cost) {
                    inverse(node, mask - root.bit);
                    return;
                }
            }

            throw new RuntimeException("What!!!");
        }

        public void spfa(int mask) {
            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                head.inque = false;
                for (int[] direction : directions) {
                    int r = head.row + direction[0];
                    int c = head.col + direction[1];
                    if (!inScope(r, c)) {
                        continue;
                    }
                    Node node = nodes[r][c];
                    long cost = head.dp[mask] + node.cost;
                    if (cost >= node.dp[mask | node.bit]) {
                        continue;
                    }
                    node.dp[mask | node.bit] = cost;
                    if ((mask & node.bit) != node.bit || node.inque) {
                        continue;
                    }
                    node.inque = true;
                    deque.addLast(node);
                }
            }
        }

        public void solve() {
            n = io.readInt();
            m = io.readInt();
            deque = new ArrayDeque<>(n * m);
            nodes = new Node[n][m];
            Node root = null;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    nodes[i][j] = new Node();
                    nodes[i][j].cost = io.readLong();
                    nodes[i][j].row = i;
                    nodes[i][j].col = j;
                    if (nodes[i][j].cost == 0) {
                        nodes[i][j].bit = 1 << (idAllocator++);
                        root = nodes[i][j];
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    nodes[i][j].dp = new long[1 << idAllocator];
                    Arrays.fill(nodes[i][j].dp, lInf);
                    nodes[i][j].dp[nodes[i][j].bit] = nodes[i][j].cost;
                }
            }
            int mask = (1 << idAllocator) - 1;
            for (int i = 0; i <= mask; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < m; k++) {
                        Node node = nodes[j][k];
                        //merge two set
                        generator.setSet(i);
                        while (generator.hasNext()) {
                            int s = generator.next();
                            int s1 = s | node.bit;
                            int s2 = (i - s) | node.bit;
                            if (s1 == i || s2 == i) {
                                continue;
                            }
                            node.dp[i] = Math.min(node.dp[i], node.dp[s1] + node.dp[s2] - node.cost);
                        }
                        //optimization from edges
                        if (node.dp[i] < lInf) {
                            deque.add(node);
                            node.inque = true;
                        }
                    }
                }
                spfa(i);
            }

            if (idAllocator == 0) {
                io.cache.append(0).append('\n');
            } else {
                inverse(root, mask);
                io.cache.append(root.dp[mask]).append('\n');
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    Node node = nodes[i][j];
                    if (!node.cover) {
                        io.cache.append('_');
                    } else if (node.cost == 0) {
                        io.cache.append('x');
                    } else {
                        io.cache.append('o');
                    }
                }
                io.cache.append('\n');
            }
        }
    }

    public static class Node {
        long[] dp;
        int id = 32;
        int bit = 0;
        long cost;
        int row;
        int col;
        boolean inque;
        boolean cover;
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
