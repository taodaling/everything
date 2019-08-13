package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BZOJ1063 {
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
        Modular mod;
        static int constant;

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
            int m = io.readInt();
            int q = io.readInt();

            mod = new Modular(q);

            Node[] nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }

            for (int i = 1; i <= m; i++) {
                Node a = nodes[io.readInt()];
                Node b = nodes[io.readInt()];
                a.next.add(b);
                b.next.add(a);
            }

            if (m != n - 1) {
                io.cache.append("-1\n-1");
                return;
            }

            findConstant(nodes[1], null);
            constant = nodes[1].dp2[2];
            io.cache.append(constant).append('\n');

            dfs(nodes[1]);
            io.cache.append(nodes[1].dp[2][constant]);
        }

        public void findConstant(Node root, Node from) {
            root.next.remove(from);
            if (root.next.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    root.dp2[i] = 0;
                }
                return;
            }

            for (Node node : root.next) {
                findConstant(node, root);
                root.dp2[2] = Math.min(Math.max(root.dp2[2], node.dp2[2] + 1),
                        Math.max(node.dp2[1], root.dp2[1]));
                root.dp2[1] = Math.min(Math.max(root.dp2[1], node.dp2[2] + 1), Math.max(node.dp2[1], root.dp2[0]));
                root.dp2[0] = Math.max(root.dp2[0], node.dp2[2] + 1);
            }

            for (int i = 1; i < 3; i++) {
                root.dp2[i] = Math.min(root.dp2[i], root.dp2[i - 1]);
            }
        }

        public void dfs(Node root) {
            if (root.next.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    Arrays.fill(root.dp[i], 1);
                }
                return;
            }

            for (Node node : root.next) {
                dfs(node);
            }

            //calc dp[0]
            for (int i = 1; i <= constant; i++) {
                root.dp[0][i] = 1;
            }
            for (Node node : root.next) {
                for (int i = 1; i <= constant; i++) {
                    root.dp[0][i] = mod.mul(root.dp[0][i], node.dp[2][i - 1]);
                }
            }

            //calc dp[1]
            for (int i = 1; i <= constant; i++) {
                int preProd = 1;
                int ans = 0;
                for (Node node : root.next) {
                    ans = mod.mul(ans, node.dp[2][i - 1]);
                    ans = mod.plus(ans, mod.mul(preProd, node.dp[1][i]));
                    preProd = mod.mul(preProd, node.dp[2][i - 1]);
                }
                root.dp[1][i] = ans;
            }

            if (root.next.size() == 1) {
                root.dp[1][0] = root.next.get(0).dp[1][0];
            }

            //calc dp[2]
            for (int i = 1; i <= constant; i++) {
                int preProd = 1;
                int ans1 = 0;
                int ans2 = 0;
                for (Node node : root.next) {
                    ans2 = mod.mul(ans2, node.dp[2][i - 1]);
                    ans2 = mod.plus(ans2, mod.mul(ans1, node.dp[1][i]));
                    ans1 = mod.mul(ans1, node.dp[2][i - 1]);
                    ans1 = mod.plus(ans1, mod.mul(preProd, node.dp[1][i]));
                    preProd = mod.mul(preProd, node.dp[2][i - 1]);
                }
                root.dp[2][i] = ans2;
            }

            if (root.next.size() == 2) {
                root.dp[2][0] =
                        mod.mul(root.next.get(0).dp[1][0],
                                root.next.get(1).dp[1][0]);
            }

            for (int i = 1; i < 3; i++) {
                for (int j = 0; j <= constant; j++) {
                    root.dp[i][j] = mod.plus(root.dp[i][j], root.dp[i - 1][j]);
                }
            }
        }
    }

    public static class Node {
        List<Node> next = new ArrayList(1);
        boolean visited;
        int[][] dp = new int[3][20];
        int[] dp2 = new int[3];
        int id;

        @Override
        public String toString() {
            return "" + id;
        }
    }


    /**
     * Mod operations
     */
    public static class Modular {
        int m;

        public Modular(int m) {
            this.m = m;
        }

        public int valueOf(int x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return x;
        }

        public int valueOf(long x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return (int) x;
        }

        public int mul(int x, int y) {
            return valueOf((long) x * y);
        }

        public int mul(long x, long y) {
            x = valueOf(x);
            y = valueOf(y);
            return valueOf(x * y);
        }

        public int plus(int x, int y) {
            return valueOf(x + y);
        }

        public int plus(long x, long y) {
            x = valueOf(x);
            y = valueOf(y);
            return valueOf(x + y);
        }

        @Override
        public String toString() {
            return "mod " + m;
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
