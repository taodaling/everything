package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LUOGU1285 {
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
        long lInf = (long) 1e18;
        double dInf = 1e50;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        int n;
        boolean[][] edges;
        int[] colors;

        public void solve() {
            n = io.readInt();
            edges = new boolean[n + 1][n + 1];
            colors = new int[n + 1];
            Arrays.fill(colors, -1);
            for (int i = 1; i <= n; i++) {
                int j;
                while ((j = io.readInt()) != 0) {
                    edges[i][j] = true;
                }
            }

            List<int[]> pairs = new ArrayList<>();
            List<List<Integer>[]> lists = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                if (colors[i] != -1) {
                    continue;
                }
                int[] p = new int[2];
                List<Integer>[] list = new List[]{new ArrayList(), new ArrayList()};
                if (!dfs(i, 0, p, list)) {
                    io.cache.append("No solution");
                    return;
                }
                pairs.add(p);
                lists.add(list);
            }

            int m = pairs.size();
            boolean[][] dp = new boolean[m + 1][n / 2 + 1];
            int[][] trace = new int[m + 1][n / 2 + 1];
            dp[0][0] = true;
            for (int j = 1; j <= m; j++) {
                int[] p = pairs.get(j - 1);
                for (int i = dp[j].length - 1; i >= 0; i--) {
                    if (get(dp[j - 1], i - p[0])) {
                        dp[j][i] = true;
                        trace[j][i] = 0;
                    } else if (get(dp[j - 1], i - p[1])) {
                        dp[j][i] = true;
                        trace[j][i] = 1;
                    }
                }
            }

            int max = 0;
            for (int i = 0; i < dp[m].length; i++) {
                if (dp[m][i]) {
                    max = Math.max(max, i);
                }
            }

            if (max == 0) {
                io.cache.append("No solution");
                return;
            }

            List<Integer> group1Ids = new ArrayList<>();
            List<Integer> group2Ids = new ArrayList<>();
            for (int i = m; i >= 1; i--) {
                group1Ids.addAll(lists.get(i - 1)[trace[i][max]]);
                group2Ids.addAll(lists.get(i - 1)[1 - trace[i][max]]);
                max -= pairs.get(i - 1)[trace[i][max]];
            }

            group1Ids.sort(Comparator.naturalOrder());
            group2Ids.sort(Comparator.naturalOrder());

            printGroup(group1Ids);
            printGroup(group2Ids);
        }

        public void printGroup(List<Integer> g) {
            io.cache.append(g.size()).append(' ');
            for (Integer i : g) {
                io.cache.append(i).append(' ');
            }
            io.cache.append('\n');
        }

        public boolean get(boolean[] dp, int i) {
            return i < 0 || i >= dp.length ? false : dp[i];
        }

        public boolean dfs(int root, int color, int[] pair, List<Integer>[] list) {
            if (colors[root] != -1) {
                return colors[root] == color;
            }
            colors[root] = color;
            list[color].add(root);
            pair[color]++;
            for (int i = 1; i <= n; i++) {
                if (i == root || (edges[i][root] && edges[root][i])) {
                    continue;
                }
                if (!dfs(i, 1 - color, pair, list)) {
                    return false;
                }
            }
            return true;
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
