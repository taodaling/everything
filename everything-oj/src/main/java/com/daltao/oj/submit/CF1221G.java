package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CF1221G {
    public static void main(String[] args) throws Exception {
        boolean local = System.getSecurityManager() == null;
        boolean async = false;

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

        BitOperator bo = new BitOperator();
        Log2 log2 = new Log2();

        Node[] nodes;
        int n;
        long[] edges;

        public void solve() {
            n = io.readInt();
            int m = io.readInt();
            edges = new long[n];
            nodes = new Node[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new Node();
            }

            for (int i = 0; i < m; i++) {
                int x = io.readInt() - 1;
                int y = io.readInt() - 1;
                edges[x] = bo.setBit(edges[x], y, true);
                edges[y] = bo.setBit(edges[y], x, true);
                nodes[x].next.add(nodes[y]);
                nodes[y].next.add(nodes[x]);
            }

            if (n == 1) {
                io.cache.append(0);
                return;
            }

            long ic = independentCount();
            int c = countConnectedComponent();
            int k = howManyNodeHasDegree0();
            boolean b = bipartite();

            long ans = 1L << n;
            ans -= ic; //not 0
            ans -= ic; //not 2
            ans -= 1L << c; //not 1
            ans += 1L << k; //not 0 and not 1
            if (b) {
                ans += 1L << c; //not 0 and not 2
            }
            ans += 1L << k; //not 1 and not 2
            if (m == 0) {
                ans -= 1L << n;
            }

            io.cache.append(ans);
        }

        public boolean bipartite() {
            boolean flag = true;
            for (int i = 0; i < n; i++) {
                if (nodes[i].color != -1) {
                    continue;
                }
                flag = flag && color(nodes[i], 0);
            }
            return flag;
        }

        public boolean color(Node root, int c) {
            if (root.color != -1) {
                return root.color == c;
            }
            root.color = c;
            for (Node node : root.next) {
                if (!color(node, 1 - c)) {
                    return false;
                }
            }
            return true;
        }

        public int howManyNodeHasDegree0() {
            int cnt = 0;
            for (int i = 0; i < n; i++) {
                if (nodes[i].next.size() == 0) {
                    cnt++;
                }
            }
            return cnt;
        }

        public int countConnectedComponent() {
            int cnt = 0;
            for (int i = 0; i < n; i++) {
                if (nodes[i].visited) {
                    continue;
                }
                cnt++;
                visit(nodes[i]);
            }
            return cnt;
        }

        public void visit(Node root) {
            if (root.visited) {
                return;
            }
            root.visited = true;
            for (Node node : root.next) {
                visit(node);
            }
        }

        public long independentCount() {
            int half = n / 2;
            int mask = (1 << half) - 1;
            boolean[] isIndependent = new boolean[mask + 1];
            isIndependent[0] = true;
            long[] mergedEdge = new long[mask + 1];
            for (int i = 1; i <= mask; i++) {
                int highestBit = log2.floorLog(i);
                isIndependent[i] = isIndependent[i - (1 << highestBit)] && bo.intersect(i, edges[highestBit]) == 0;
                mergedEdge[i] = mergedEdge[i - (1 << highestBit)] | edges[highestBit];
            }

            int otherHalf = n - half;
            int otherHalfMask = (1 << otherHalf) - 1;
            int[] isOtherHalfIndependent = new int[otherHalfMask + 1];
            isOtherHalfIndependent[0] = 1;
            for (int i = 1; i <= otherHalfMask; i++) {
                int highestBit = log2.floorLog(i);
                isOtherHalfIndependent[i] = isOtherHalfIndependent[i - (1 << highestBit)] == 1 && bo.intersect(i, edges[highestBit + half] >>> half) == 0
                        ? 1 : 0;
            }
            FastWalshHadamardTransform.orFWT(isOtherHalfIndependent, 0, otherHalfMask);

            long ans = 0;
            for (int i = 0; i <= mask; i++) {
                if (!isIndependent[i]) {
                    continue;
                }
                int invMask = (int) (mergedEdge[i] >>> half);
                invMask = otherHalfMask - invMask;
                ans += isOtherHalfIndependent[invMask];
            }

            return ans;
        }
    }

    public static class Node {
        List<Node> next = new ArrayList<>();
        boolean visited;
        int color = -1;
    }

    public static class FastWalshHadamardTransform {
        public static void orFWT(int[] p, int l, int r) {
            if (l == r) {
                return;
            }
            int m = (l + r) >> 1;
            orFWT(p, l, m);
            orFWT(p, m + 1, r);
            for (int i = 0, until = m - l; i <= until; i++) {
                int a = p[l + i];
                int b = p[m + 1 + i];
                p[m + 1 + i] = a + b;
            }
        }

        public static void orIFWT(int[] p, int l, int r) {
            if (l == r) {
                return;
            }
            int m = (l + r) >> 1;
            for (int i = 0, until = m - l; i <= until; i++) {
                int a = p[l + i];
                int b = p[m + 1 + i];
                p[m + 1 + i] = b - a;
            }
            orIFWT(p, l, m);
            orIFWT(p, m + 1, r);
        }

        public static void andFWT(int[] p, int l, int r) {
            if (l == r) {
                return;
            }
            int m = (l + r) >> 1;
            andFWT(p, l, m);
            andFWT(p, m + 1, r);
            for (int i = 0, until = m - l; i <= until; i++) {
                int a = p[l + i];
                int b = p[m + 1 + i];
                p[l + i] = a + b;
            }
        }

        public static void andIFWT(int[] p, int l, int r) {
            if (l == r) {
                return;
            }
            int m = (l + r) >> 1;
            for (int i = 0, until = m - l; i <= until; i++) {
                int a = p[l + i];
                int b = p[m + 1 + i];
                p[l + i] = a - b;
            }
            andIFWT(p, l, m);
            andIFWT(p, m + 1, r);
        }

        public static void xorFWT(int[] p, int l, int r) {
            if (l == r) {
                return;
            }
            int m = (l + r) >> 1;
            xorFWT(p, l, m);
            xorFWT(p, m + 1, r);
            for (int i = 0, until = m - l; i <= until; i++) {
                int a = p[l + i];
                int b = p[m + 1 + i];
                p[l + i] = a + b;
                p[m + 1 + i] = a - b;
            }
        }

        public static void xorIFWT(int[] p, int l, int r) {
            if (l == r) {
                return;
            }
            int m = (l + r) >> 1;
            for (int i = 0, until = m - l; i <= until; i++) {
                int a = p[l + i];
                int b = p[m + 1 + i];
                p[l + i] = (a + b) / 2;
                p[m + 1 + i] = (a - b) / 2;
            }
            xorIFWT(p, l, m);
            xorIFWT(p, m + 1, r);
        }

        public static void dotMul(int[] a, int[] b, int n) {
            for (int i = 0; i < n; i++) {
                a[i] = a[i] * b[i];
            }
        }
    }

    /**
     * Log operations
     */
    public static class Log2 {
        public int ceilLog(int x) {
            return 32 - Integer.numberOfLeadingZeros(x - 1);
        }

        public int floorLog(int x) {
            return 31 - Integer.numberOfLeadingZeros(x);
        }

        public int ceilLog(long x) {
            return 64 - Long.numberOfLeadingZeros(x - 1);
        }

        public int floorLog(long x) {
            return 63 - Long.numberOfLeadingZeros(x);
        }
    }

    /**
     * Bit operations
     */
    public static class BitOperator {
        public int bitAt(int x, int i) {
            return (x >> i) & 1;
        }

        public int bitAt(long x, int i) {
            return (int) ((x >> i) & 1);
        }

        public int setBit(int x, int i, boolean v) {
            if (v) {
                x |= 1 << i;
            } else {
                x &= ~(1 << i);
            }
            return x;
        }

        public long setBit(long x, int i, boolean v) {
            if (v) {
                x |= 1L << i;
            } else {
                x &= ~(1L << i);
            }
            return x;
        }

        public long swapBit(long x, int i, int j) {
            int bi = bitAt(x, i);
            int bj = bitAt(x, j);
            x = setBit(x, i, bj == 1);
            x = setBit(x, j, bi == 1);
            return x;
        }

        public int swapBit(int x, int i, int j) {
            int bi = bitAt(x, i);
            int bj = bitAt(x, j);
            x = setBit(x, i, bj == 1);
            x = setBit(x, j, bi == 1);
            return x;
        }

        /**
         * Determine whether x is subset of y
         */
        public boolean subset(long x, long y) {
            return intersect(x, y) == x;
        }

        /**
         * Merge two set
         */
        public long merge(long x, long y) {
            return x | y;
        }

        public long intersect(long x, long y) {
            return x & y;
        }

        public long differ(long x, long y) {
            return x - intersect(x, y);
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
