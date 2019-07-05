package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BZOJ3531V2 {
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
            int q = io.readInt();
            HeavyLightDecompose hld = new HeavyLightDecompose(n, 0);
            for (int i = 0; i < n; i++) {
                int w = io.readInt();
                int c = io.readInt();
                hld.setInitVal(i, c, w);
            }
            for (int i = 1; i < n; i++) {
                hld.addEdge(io.readInt() - 1, io.readInt() - 1);
            }
            hld.finish();

            char[] cmd = new char[10];
            for (int i = 0; i < q; i++) {
                io.readString(cmd, 0);
                int x = io.readInt();
                int y = io.readInt();
                switch (cmd[1]) {
                    case 'S':
                        io.cache.append(hld.processSumQuery(x - 1, y - 1)).append('\n');
                        break;
                    case 'M':
                        io.cache.append(hld.processMaxQuery(x - 1, y - 1)).append('\n');
                        break;
                    case 'C':
                        hld.updateC(x - 1, y);
                        break;
                    case 'W':
                        hld.updateW(x - 1, y);
                        break;
                }
            }
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

    public static class HeavyLightDecompose {
        private static long op(long a, long b) {
            return a + b;
        }

        public static class Segment implements Cloneable {
            private static final Segment NIL = new Segment();

            static {
                NIL.left = NIL.right = NIL;
            }

            private Segment left;
            private Segment right;
            private int sum;
            private int max;

            public void pushUp() {
                sum = left.sum + right.sum;
                max = Math.max(left.max, right.max);
            }

            public void pushDown() {
                left = left.clone();
                right = right.clone();
            }

            public Segment() {

            }

            private boolean covered(int ll, int rr, int l, int r) {
                return ll <= l && rr >= r;
            }

            private boolean noIntersection(int ll, int rr, int l, int r) {
                return ll > r || rr < l;
            }

            public void update(int ll, int rr, int l, int r, int val) {
                if (noIntersection(ll, rr, l, r)) {
                    return;
                }
                if (covered(ll, rr, l, r)) {
                    max = sum = val;
                    return;
                }
                pushDown();
                int m = (l + r) >> 1;
                left.update(ll, rr, l, m, val);
                right.update(ll, rr, m + 1, r, val);
                pushUp();
            }

            public int querySum(int ll, int rr, int l, int r) {
                if (noIntersection(ll, rr, l, r)) {
                    return 0;
                }
                if (covered(ll, rr, l, r)) {
                    return sum;
                }
                pushDown();
                int m = (l + r) >> 1;
                return left.querySum(ll, rr, l, m) +
                        right.querySum(ll, rr, m + 1, r);
            }

            public int queryMax(int ll, int rr, int l, int r) {
                if (noIntersection(ll, rr, l, r)) {
                    return 0;
                }
                if (covered(ll, rr, l, r)) {
                    return max;
                }
                pushDown();
                int m = (l + r) >> 1;
                return Math.max(left.queryMax(ll, rr, l, m),
                        right.queryMax(ll, rr, m + 1, r));
            }

            @Override
            public Segment clone() {
                if (this != NIL) {
                    return this;
                }
                try {
                    return (Segment) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public static class HLDNode {
            List<HLDNode> next = new ArrayList(2);
            int id;
            int dfsOrderFrom;
            int dfsOrderTo;
            int size;
            int w;
            int c;
            HLDNode link;
            HLDNode heavy;
            HLDNode father;

            @Override
            public String toString() {
                return "" + id;
            }
        }

        public HeavyLightDecompose(int n, int rootId) {
            this.n = n;
            nodes = new HLDNode[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new HLDNode();
                nodes[i].id = i;
            }
            root = nodes[rootId];
        }

        public void addEdge(int a, int b) {
            nodes[a].next.add(nodes[b]);
            nodes[b].next.add(nodes[a]);
        }

        public void setInitVal(int nodeId, int c, int w) {
            nodes[nodeId].c = c;
            nodes[nodeId].w = w;
        }

        public void updateC(int nodeId, int c) {
            HLDNode node = nodes[nodeId];
            segments[node.c] = segments[node.c].clone();
            segments[node.c].update(node.dfsOrderFrom, node.dfsOrderFrom, 1, n, 0);
            node.c = c;
            segments[node.c] = segments[node.c].clone();
            segments[node.c].update(node.dfsOrderFrom, node.dfsOrderFrom, 1, n, node.w);
        }

        public void updateW(int nodeId, int w) {
            HLDNode node = nodes[nodeId];
            node.w = w;
            segments[node.c] = segments[node.c].clone();
            segments[node.c].update(node.dfsOrderFrom, node.dfsOrderFrom, 1, n, node.w);
        }

        public void finish() {
            dfs(root, null);
            dfs2(root, root);
            segments = new Segment[100000 + 1];
            Arrays.fill(segments, Segment.NIL);
            for (HLDNode node : nodes) {
                segments[node.c] = segments[node.c].clone();
                segments[node.c].update(node.dfsOrderFrom, node.dfsOrderFrom, 1, n, node.w);
            }
        }

        public int processMaxQuery(int uId, int vId) {
            HLDNode u = nodes[uId];
            HLDNode v = nodes[vId];
            Segment segment = segments[u.c];
            int max = 0;
            while (u != v) {
                if (u.link == v.link) {
                    if (u.size > v.size) {
                        HLDNode tmp = u;
                        u = v;
                        v = tmp;
                    }
                    max = Math.max(max, segment.queryMax(v.dfsOrderFrom + 1, u.dfsOrderFrom, 1, n));
                    u = v;
                } else {
                    if (u.link.size > v.link.size) {
                        HLDNode tmp = u;
                        u = v;
                        v = tmp;
                    }
                    max = Math.max(max, segment.queryMax(u.link.dfsOrderFrom, u.dfsOrderFrom, 1, n));
                    u = u.link.father;
                }
            }
            max = Math.max(max, segment.queryMax(u.dfsOrderFrom, u.dfsOrderFrom, 1, n));
            return max;
        }

        public long processSumQuery(int uId, int vId) {
            HLDNode u = nodes[uId];
            HLDNode v = nodes[vId];
            Segment segment = segments[u.c];
            long sum = 0;
            while (u != v) {
                if (u.link == v.link) {
                    if (u.size > v.size) {
                        HLDNode tmp = u;
                        u = v;
                        v = tmp;
                    }
                    sum = (sum + segment.querySum(v.dfsOrderFrom + 1, u.dfsOrderFrom, 1, n));
                    u = v;
                } else {
                    if (u.link.size > v.link.size) {
                        HLDNode tmp = u;
                        u = v;
                        v = tmp;
                    }
                    sum = (sum + segment.querySum(u.link.dfsOrderFrom, u.dfsOrderFrom, 1, n));
                    u = u.link.father;
                }
            }
            sum = (sum + segment.querySum(u.dfsOrderFrom, u.dfsOrderFrom, 1, n));
            return sum;
        }

        private static void dfs(HLDNode root, HLDNode father) {
            root.size = 1;
            root.father = father;
            for (HLDNode node : root.next) {
                if (node == father) {
                    continue;
                }
                dfs(node, root);
                root.size += node.size;
                if (root.heavy == null || root.heavy.size < node.size) {
                    root.heavy = node;
                }
            }
        }

        private void dfs2(HLDNode root, HLDNode link) {
            root.dfsOrderFrom = order++;
            root.link = link;
            if (root.heavy != null) {
                dfs2(root.heavy, link);
            }
            for (HLDNode node : root.next) {
                if (node == root.father || node == root.heavy) {
                    continue;
                }
                dfs2(node, node);
            }
            root.dfsOrderTo = order - 1;
        }

        int n;
        int order = 1;
        HLDNode root;
        HLDNode[] nodes;
        Segment[] segments;
    }
}
