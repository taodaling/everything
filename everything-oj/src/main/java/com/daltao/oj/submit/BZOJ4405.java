package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BZOJ4405 {
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
            while (t-- > 0)
                solve();
        }

        int n;
        int m;

        public int idOfBall(int i) {
            return i;
        }

        public int idOfSlot(int i, int j) {
            return n + (i - 1) * 3 + j;
        }

        public int inverseBallId(int i) {
            return i + 1;
        }

        public int inverseSlotId(int i) {
            return (i - n - 1) / 3 + 1;
        }

        public void solve() {
            n = io.readInt();
            m = io.readInt();
            int e = io.readInt();

            EdmondBlossom eb = new EdmondBlossom(n + 3 * m);
            for (int i = 1; i <= m; i++) {
                eb.addEdge(idOfSlot(i, 1), idOfSlot(i, 2));
                eb.addEdge(idOfSlot(i, 2), idOfSlot(i, 3));
                eb.addEdge(idOfSlot(i, 3), idOfSlot(i, 1));
            }

            for (int i = 1; i <= e; i++) {
                int v = io.readInt();
                int u = io.readInt();
                eb.addEdge(idOfBall(v), idOfSlot(u, 1));
                eb.addEdge(idOfBall(v), idOfSlot(u, 2));
                eb.addEdge(idOfBall(v), idOfSlot(u, 3));
            }

            io.cache.append(eb.maxMatch() - n).append('\n');
            for (int i = 1; i <= n; i++) {
                io.cache.append(inverseSlotId(eb.mateOf(idOfBall(i)))).append(' ');
            }
            io.cache.append('\n');
        }
    }

    public static class EdmondBlossom {
        int n;
        int[] pre;
        boolean[][] edges;
        int[] mate;
        int[] link;
        int[] vis;
        int[] fa;
        int[] que;
        int hd;
        int tl;
        int[] ss;
        int tim;

        public int mateOf(int i) {
            return mate[i];
        }

        public void addEdge(int x, int y) {
            edges[x][y] = edges[y][x] = true;
        }

        private int find(int x) {
            return fa[x] == x ? x : (fa[x] = find(fa[x]));
        }

        private int lca(int x, int y) {
            ++tim;
            while (ss[x] != tim) {
                if (x != 0) {
                    ss[x] = tim;
                    x = find(link[mate[x]]);
                }
                int tmp = x;
                x = y;
                y = tmp;
            }
            return x;
        }

        private void flower(int x, int y, int p) {
            while (find(x) != p) {
                link[x] = y;
                fa[y = mate[x]] = fa[x] = p;
                if (vis[y] == 1) vis[que[tl++] = y] = 2;
                x = link[y];
            }
        }

        public boolean match(int x) {
            hd = tl = 0;
            for (int i = 1; i <= n; ++i) vis[fa[i] = i] = 0;
            vis[que[tl++] = x] = 2;
            while (hd < tl) {
                x = que[hd++];
                for (int u = 1; u <= n; u++) {
                    if (!edges[x][u]) {
                        continue;
                    }
                    if (0 == vis[u]) {
                        vis[u] = 1;
                        link[u] = x;
                        if (0 == mate[u]) {
                            while (0 != x) {
                                x = mate[link[u]];
                                mate[mate[u] = link[u]] = u;
                                u = x;
                            }
                            return true;
                        } else
                            vis[que[tl++] = mate[u]] = 2;
                    } else if (vis[u] == 2 && find(u) != find(x)) {
                        int p = lca(x, u);
                        flower(x, u, p);
                        flower(u, x, p);
                    }
                }
            }
            return false;
        }

        public int maxMatch() {
            int total = 0;
            for (int i = 1; i <= n; i++) {
                if (mate[i] == 0
                        && match(i)) {
                    total++;
                }
            }
            return total;
        }

        public EdmondBlossom(int n) {
            this.n = n;
            int len = n + 1;
            pre = new int[len];
            edges = new boolean[len][len];
            mate = new int[len];
            link = new int[len];
            vis = new int[len];
            fa = new int[len];
            que = new int[len];
            ss = new int[len];
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
