package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class BZOJ2244 {
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
        Segment segment;
        int segmentLeftBound = 0;
        int segmentRightBound;


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
            Node[] nodes = new Node[n + 1];
            int[] vals = new int[2 * n];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].i = i;
                nodes[i].h = -io.readInt();
                nodes[i].v = -io.readInt();
                vals[i - 1] = nodes[i].h;
                vals[i + n - 1] = nodes[i].v;
            }

            DiscreteMap map = new DiscreteMap(vals, 0, vals.length);
            for (int i = 1; i <= n; i++) {
                nodes[i].h = map.rankOf(nodes[i].h);
                nodes[i].v = map.rankOf(nodes[i].v);
            }
            segmentLeftBound = map.minPossibleRank();
            segmentRightBound = map.maxPossibleRank();
            segment = Segment.build(segmentLeftBound, segmentRightBound);
            cdqLdp(nodes, 1, n);
            cdqRdp(nodes, 1, n);
            Arrays.sort(nodes, 1, n + 1, Node.sortByI);

            int maxDp = 0;
            for (int i = 1; i <= n; i++) {
                maxDp = Math.max(maxDp, nodes[i].ldp + nodes[i].rdp - 1);
            }

            double total = 0;
            for (int i = 1; i <= n; i++) {
                if (nodes[i].ldp + nodes[i].rdp - 1 != maxDp) {
                    continue;
                }
                if (nodes[i].ldp != 1) {
                    continue;
                }
                total += nodes[i].lway * nodes[i].rway;
            }

            io.cache.append(maxDp).append('\n');
            for (int i = 1; i <= n; i++) {
                if (nodes[i].ldp + nodes[i].rdp - 1 != maxDp) {
                    io.cache.append(0).append(' ');
                    continue;
                }
                io.cache.append(nodes[i].lway / total * nodes[i].rway).append(' ');
            }
        }

        public void cdqLdp(Node[] nodes, int f, int t) {
            if (f == t) {
                nodes[f].setLMax(1, 1);
                return;
            }
            Arrays.sort(nodes, f, t + 1, Node.sortByI);
            int m = (f + t) >> 1;
            cdqLdp(nodes, f, m);
            Arrays.sort(nodes, f, m + 1, Node.sortByH);
            Arrays.sort(nodes, m + 1, t + 1, Node.sortByH);

            int i = f;
            int j = m + 1;
            segment.clear();
            while (j <= t) {
                while (i <= m && nodes[i].h <= nodes[j].h) {
                    Segment.update(nodes[i].v, nodes[i].v, segmentLeftBound, segmentRightBound, nodes[i].ldp, nodes[i].lway, segment);
                    i++;
                }
                Segment.prepareForQuery();
                Segment.query(0, nodes[j].v, segmentLeftBound, segmentRightBound, segment);
                nodes[j].setLMax(Segment.queryMax + 1, Segment.queryCount);
                j++;
            }
            cdqLdp(nodes, m + 1, t);
        }

        public void cdqRdp(Node[] nodes, int f, int t) {
            if (f == t) {
                nodes[f].setRMax(1, 1);
                return;
            }
            Arrays.sort(nodes, f, t + 1, Node.sortByI);
            int m = (f + t) >> 1;
            cdqRdp(nodes, m + 1, t);
            Arrays.sort(nodes, f, m + 1, Node.sortByH);
            Arrays.sort(nodes, m + 1, t + 1, Node.sortByH);

            int i = m;
            int j = t;
            segment.clear();
            while (i >= f) {
                while (j > m && nodes[i].h <= nodes[j].h) {
                    Segment.update(nodes[j].v, nodes[j].v, segmentLeftBound, segmentRightBound, nodes[j].rdp, nodes[j].rway, segment);
                    j--;
                }
                Segment.prepareForQuery();
                Segment.query(nodes[i].v, segmentRightBound, segmentLeftBound, segmentRightBound, segment);
                nodes[i].setRMax(Segment.queryMax + 1, Segment.queryCount);
                i--;
            }
            cdqRdp(nodes, f, m);
        }
    }


    public static class Node {
        int i;
        int h;
        int v;
        int ldp;
        int rdp;
        double lway;
        double rway;

        public void setLMax(int ldp, double count) {
            if (this.ldp < ldp) {
                this.ldp = ldp;
                lway = 0;
            }
            if (this.ldp == ldp) {
                lway += count;
            }
        }

        public void setRMax(int rdp, double count) {
            if (this.rdp < rdp) {
                this.rdp = rdp;
                rway = 0;
            }
            if (this.rdp == rdp) {
                rway += count;
            }
        }

        public static Comparator<Node> sortByH = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.h - o2.h;
            }
        };

        public static Comparator<Node> sortByI = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.i - o2.i;
            }
        };

        @Override
        public String toString() {
            return "" + i;
        }
    }

    public static class Segment {
        Segment left;
        Segment right;
        int max;
        double count;
        boolean clear;

        static int queryMax;
        static double queryCount;

        public void setMax(int max, double c) {
            if (this.max < max) {
                this.max = max;
                count = 0;
            }

            if (this.max == max) {
                count += c;
            }
        }

        public void clear() {
            clear = true;
            max = 0;
            count = 0;
        }

        public static Segment build(int l, int r) {
            Segment segment = new Segment();
            if (l != r) {
                int m = (l + r) >> 1;
                segment.left = build(l, m);
                segment.right = build(m + 1, r);
                segment.pushUp();
            }
            return segment;
        }

        public static boolean checkOutOfRange(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public static boolean checkCoverage(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        public static void update(int ll, int rr, int l, int r, int max, double c, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return;
            }
            if (checkCoverage(ll, rr, l, r)) {
                segment.setMax(max, c);
                return;
            }
            int m = (l + r) >> 1;

            segment.pushDown();
            update(ll, rr, l, m, max, c, segment.left);
            update(ll, rr, m + 1, r, max, c, segment.right);
            segment.pushUp();
        }

        public void query() {
            if (queryMax < max) {
                queryMax = max;
                queryCount = 0;
            }
            if (queryMax == max) {
                queryCount += count;
            }
        }

        public static void prepareForQuery() {
            queryMax = 0;
            queryCount = 0;
        }

        public static void query(int ll, int rr, int l, int r, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return;
            }
            if (checkCoverage(ll, rr, l, r)) {
                segment.query();
                return;
            }
            int m = (l + r) >> 1;
            segment.pushDown();
            query(ll, rr, l, m, segment.left);
            query(ll, rr, m + 1, r, segment.right);
        }

        public void pushDown() {
            if (clear) {
                clear = false;
                left.clear();
                right.clear();
            }
        }

        public void pushUp() {
            max = Math.max(left.max, right.max);
            count = 0;
            if (max == left.max) {
                count += left.count;
            }
            if (max == right.max) {
                count += right.count;
            }
        }

    }

    public static class Randomized {
        static Random random = new Random();

        public static double nextDouble(double min, double max) {
            return random.nextDouble() * (max - min) + min;
        }

        public static void randomizedArray(int[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                int tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(long[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                long tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(double[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                double tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(float[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                float tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static <T> void randomizedArray(T[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                T tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static int nextInt(int l, int r) {
            return random.nextInt(r - l + 1) + l;
        }
    }

    public static class DiscreteMap {
        int[] val;
        int f;
        int t;

        public DiscreteMap(int[] val, int f, int t) {
            Randomized.randomizedArray(val, f, t);
            Arrays.sort(val, f, t);
            int wpos = f + 1;
            for (int i = f + 1; i < t; i++) {
                if (val[i] == val[i - 1]) {
                    continue;
                }
                val[wpos++] = val[i];
            }
            this.val = val;
            this.f = f;
            this.t = wpos;
        }

        public int rankOf(int x) {
            return Arrays.binarySearch(val, f, t, x);
        }

        public int maxPossibleRank() {
            return t - f - 1;
        }

        public int minPossibleRank() {
            return 0;
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
}
