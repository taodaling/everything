package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LUOGU4294 {
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


        boolean[][] selected;

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();
            int[][] grids = new int[n][m];
            int keyCnt = 0;
            Node[][][] nodes = new Node[1 << 10][n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    grids[i][j] = io.readInt();

                    for (int k = 0; k < (1 << 10); k++) {
                        Node[][] nodesWithState = nodes[k];
                        nodesWithState[i][j] = new Node();
                        nodesWithState[i][j].i = i;
                        nodesWithState[i][j].j = j;
                        nodesWithState[i][j].bits = k;
                        nodesWithState[i][j].mask = grids[i][j] == 0 ?
                                (1 << keyCnt) : 0;
                    }

                    if (grids[i][j] == 0) {
                        keyCnt++;
                    }
                }
            }

            SubsetGenerator ss = new SubsetGenerator();
            Deque<Node> deque = new ArrayDeque<>(n * m);
            int[][] ways = new int[][]{
                    {1, 0}, {0, 1}, {-1, 0}, {0, -1}
            };
            for (int i = 0; i < (1 << keyCnt); i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < m; k++) {
                        Node node = nodes[i][j][k];
                        node.dp = i == node.mask ? grids[j][k] : inf;
                        deque.addLast(node);
                        node.inque = true;
                    }
                }
                ss.setSet(i);
                while (ss.hasNext()) {
                    int s = ss.next();
                    if (s == 0 || s == i) {
                        continue;
                    }
                    for (int j = 0; j < n; j++) {
                        for (int k = 0; k < m; k++) {
                            Node node = nodes[i][j][k];
                            if((node.mask | i) != i){
                                continue;
                            }
                            if (node.dp > nodes[s | node.mask][j][k].dp
                                    + nodes[(i - s) | node.mask][j][k].dp - grids[j][k]) {
                                node.dp = nodes[s | node.mask][j][k].dp
                                        + nodes[(i - s) | node.mask][j][k].dp - grids[j][k];
                                node.l = nodes[s | node.mask][j][k];
                                node.r = nodes[(i - s) | node.mask][j][k];
                            }
                        }
                    }
                }
                while (!deque.isEmpty()) {
                    Node head = deque.removeFirst();
                    head.inque = false;
                    for (int[] way : ways) {
                        int jj = way[0] + head.i;
                        int kk = way[1] + head.j;
                        if (jj < 0 || kk < 0 || jj >= n || kk >= m) {
                            continue;
                        }
                        if((i | nodes[0][jj][kk].mask) != i){
                            continue;
                        }
                        if (nodes[i][jj][kk].dp <= head.dp + grids[jj][kk]) {
                            continue;
                        }
                        nodes[i][jj][kk].dp = head.dp + grids[jj][kk];
                        nodes[i][jj][kk].r = null;
                        nodes[i][jj][kk].l = head;
                        if (nodes[i][jj][kk].inque) {
                            continue;
                        }
                        nodes[i][jj][kk].inque = true;
                        deque.addLast(nodes[i][jj][kk]);
                    }
                }
            }

            int endMask = (1 << keyCnt) - 1;
            Node minNode = nodes[endMask][0][0];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (minNode.dp > nodes[endMask][i][j].dp) {
                        minNode = nodes[endMask][i][j];
                    }
                }
            }

            io.cache.append(minNode.dp).append('\n');
            selected = new boolean[n][m];
            dfs(minNode);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (!selected[i][j]) {
                        io.cache.append('_');
                    } else if (grids[i][j] == 0) {
                        io.cache.append('x');
                    } else {
                        io.cache.append('o');
                    }
                }
                io.cache.append('\n');
            }
        }

        public void dfs(Node node) {
            if (node == null) {
                return;
            }
            selected[node.i][node.j] = true;
            dfs(node.l);
            dfs(node.r);
        }
    }


    public static class Node {
        int mask;
        int i;
        int j;
        int dp;
        boolean inque;

        int bits;
        Node l;
        Node r;

        @Override
        public String toString() {
            return String.format("(%d,%d)", i, j);
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
