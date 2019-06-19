package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BZOJ2243 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = true;

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
            int m = io.readInt();
            HeavyLightDecompose hld = new HeavyLightDecompose(n, 0);
            for (int i = 0; i < n; i++) {
                hld.setInitVal(i, io.readInt());
            }
            for (int i = 1; i < n; i++) {
                int a = io.readInt() - 1;
                int b = io.readInt() - 1;
                hld.addEdge(a, b);
            }

            hld.finish();
            for (int i = 0; i < m; i++) {
                char type = io.readChar();
                int a = io.readInt() - 1;
                int b = io.readInt() - 1;
                if (type == 'C') {
                    hld.update(a, b, io.readInt());
                } else {
                    int color = hld.query(a, b);
                    io.cache.append(color).append('\n');
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


    public static class HeavyLightDecompose {
        public static class Segment {
            private Segment left;
            private Segment right;
            private int val;
            private int tag;
            private int sameColor = -1;

            public void setSameColor(int c) {
                if (c == -1) {
                    return;
                }
                sameColor = c;
                val = c;
                tag = 0;
            }

            public void setTag(int tag) {
                if (tag == -1) {
                    return;
                }
                this.tag = tag;
            }

            public void pushUp() {
                tag = left.tag + right.tag;
            }

            public void pushDown() {
                if (sameColor >= 0) {
                    left.setSameColor(sameColor);
                    right.setSameColor(sameColor);
                    sameColor = -1;
                }
            }

            public Segment(int l, int r, int[] vals, int[] tags) {
                if (l < r) {
                    int m = (l + r) >> 1;
                    left = new Segment(l, m, vals, tags);
                    right = new Segment(m + 1, r, vals, tags);
                    pushUp();
                } else {
                    val = vals[l];
                    tag = tags[l];
                }
            }

            private boolean covered(int ll, int rr, int l, int r) {
                return ll <= l && rr >= r;
            }

            private boolean noIntersection(int ll, int rr, int l, int r) {
                return ll > r || rr < l;
            }

            public void update(int ll, int rr, int l, int r, int sameColor, int tag) {
                if (noIntersection(ll, rr, l, r)) {
                    return;
                }
                if (covered(ll, rr, l, r)) {
                    setSameColor(sameColor);
                    setTag(tag);
                    return;
                }
                pushDown();
                int m = (l + r) >> 1;
                left.update(ll, rr, l, m, sameColor, tag);
                right.update(ll, rr, m + 1, r, sameColor, tag);
                pushUp();
            }

            public int queryTag(int ll, int rr, int l, int r) {
                if (noIntersection(ll, rr, l, r)) {
                    return 0;
                }
                if (covered(ll, rr, l, r)) {
                    return tag;
                }
                pushDown();
                int m = (l + r) >> 1;
                return (left.queryTag(ll, rr, l, m) +
                        right.queryTag(ll, rr, m + 1, r));
            }

            public int queryColor(int ll, int rr, int l, int r) {
                if (noIntersection(ll, rr, l, r)) {
                    return -1;
                }
                if (covered(ll, rr, l, r)) {
                    return val;
                }
                pushDown();
                int m = (l + r) >> 1;
                return Math.max(left.queryColor(ll, rr, l, m),
                        right.queryColor(ll, rr, m + 1, r));
            }
        }

        public static class HLDNode {
            List<HLDNode> next = new ArrayList(2);
            int id;
            int dfsOrderFrom;
            int dfsOrderTo;
            int size;
            int val;
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

        public void setInitVal(int nodeId, int val) {
            nodes[nodeId].val = val;
        }

        public void finish() {
            dfs(root, null);
            dfs2(root, root);
            int[] colors = new int[n + 1];
            int[] tags = new int[n + 1];
            for (int i = 0; i < n; i++) {
                colors[nodes[i].dfsOrderFrom] = nodes[i].val;
                if (nodes[i].link != nodes[i] && nodes[i].val != nodes[i].father.val) {
                    tags[nodes[i].dfsOrderFrom] = 1;
                }
            }
            segment = new Segment(1, n, colors, tags);
        }

        private void update0(HLDNode from, HLDNode to, int color) {
            if (to.dfsOrderFrom > from.dfsOrderFrom) {
                return;
            }
            segment.update(to.dfsOrderFrom, from.dfsOrderFrom, 1, n, color, -1);

            if (from.heavy != null) {
                if (segment.queryColor(from.heavy.dfsOrderFrom, from.heavy.dfsOrderFrom, 1, n)
                        != color) {
                    segment.update(from.heavy.dfsOrderFrom, from.heavy.dfsOrderFrom, 1, n, -1, 1);
                } else {
                    segment.update(from.heavy.dfsOrderFrom, from.heavy.dfsOrderFrom, 1, n, -1, 0);
                }
            }

            if (to.link != to) {
                if (segment.queryColor(to.father.dfsOrderFrom, to.father.dfsOrderFrom, 1, n)
                        != color) {
                    segment.update(to.dfsOrderFrom, to.dfsOrderFrom, 1, n, -1, 1);
                }
            }
        }

        public void update(int uId, int vId, int color) {
            HLDNode u = nodes[uId];
            HLDNode v = nodes[vId];
            while (u != v) {
                if (u.link == v.link) {
                    if (u.size > v.size) {
                        HLDNode tmp = u;
                        u = v;
                        v = tmp;
                    }
                    update0(u, v.heavy, color);
                    u = v;
                } else {
                    if (u.link.size > v.link.size) {
                        HLDNode tmp = u;
                        u = v;
                        v = tmp;
                    }
                    update0(u, u.link, color);
                    segment.queryTag(u.link.dfsOrderFrom, u.dfsOrderFrom, 1, n);
                    u = u.link.father;
                }
            }
            update0(u, u, color);
            return;
        }

        public int query(int uId, int vId) {
            HLDNode u = nodes[uId];
            HLDNode v = nodes[vId];
            int tag = 1;
            while (u != v) {
                if (u.link == v.link) {
                    if (u.size > v.size) {
                        HLDNode tmp = u;
                        u = v;
                        v = tmp;
                    }
                    tag = tag + segment.queryTag(v.dfsOrderFrom + 1, u.dfsOrderFrom, 1, n);
                    u = v;
                } else {
                    if (u.link.size > v.link.size) {
                        HLDNode tmp = u;
                        u = v;
                        v = tmp;
                    }
                    tag = tag + segment.queryTag(u.link.dfsOrderFrom, u.dfsOrderFrom, 1, n);
                    if (segment.queryColor(u.link.dfsOrderFrom, u.link.dfsOrderFrom, 1, n) !=
                            segment.queryColor(u.link.father.dfsOrderFrom, u.link.father.dfsOrderFrom, 1, n)) {
                        tag++;
                    }
                    u = u.link.father;
                }
            }
            return tag;
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
        Segment segment;
    }
}
