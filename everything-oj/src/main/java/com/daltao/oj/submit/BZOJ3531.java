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
import java.util.Random;

public class BZOJ3531 {
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

        public int statusOf(int w, int c) {
            return c * 20000 + w;
        }

        public int wOf(int status) {
            return status % 20000;
        }

        public int cOf(int status) {
            return status / 20000;
        }

        public void solve() {
            int n = io.readInt();
            int q = io.readInt();
            final int[] status = new int[n];

            int[] allValues = new int[n + q];
            int allValuesTail = 0;
            for (int i = 0; i < n; i++) {
                status[i] = statusOf(io.readInt(), io.readInt());
                allValues[allValuesTail++] = status[i];
            }
            ModifiableMoOnTree mo = new ModifiableMoOnTree(n);
            for (int i = 1; i < n; i++) {
                int x = io.readInt() - 1;
                int y = io.readInt() - 1;
                mo.addEdge(x, y);
            }


            List<ModifiableMoOnTree.Query> qList = new ArrayList(q);
            List<ModifiableMoOnTree.Modification> mList = new ArrayList(q);
            char[] cmd = new char[10];
            for (int i = 0; i < q; i++) {
                io.readString(cmd, 0);
                int x = io.readInt();
                int y = io.readInt();
                if (cmd[1] == 'C') {
                    ModifiableMoOnTree.Modification modification = new ModifiableMoOnTree.Modification();
                    modification.x = x - 1;
                    modification.from = status[modification.x];
                    modification.to = status[modification.x] = statusOf(wOf(modification.from), y);
                    mList.add(modification);
                    allValues[allValuesTail++] = modification.to;
                } else if (cmd[1] == 'W') {
                    ModifiableMoOnTree.Modification modification = new ModifiableMoOnTree.Modification();
                    modification.x = x - 1;
                    modification.from = status[modification.x];
                    modification.to = status[modification.x] = statusOf(y, cOf(modification.from));
                    mList.add(modification);
                    allValues[allValuesTail++] = modification.to;
                } else {
                    ModifiableMoOnTree.Query query = new ModifiableMoOnTree.Query();
                    query.u = x - 1;
                    query.v = y - 1;
                    query.c = cOf(status[query.u]);
                    query.t = cmd[1] == 'S' ? 0 : 1;
                    query.version = mList.size();
                    qList.add(query);
                }
            }

            final DiscreteMap dm = new DiscreteMap(allValues, 0, allValuesTail);
            for (int i = 0; i < n; i++) {
                status[i] = dm.rankOf(status[i]);
            }
            for (ModifiableMoOnTree.Modification modification : mList) {
                modification.from = dm.rankOf(modification.from);
                modification.to = dm.rankOf(modification.to);
            }

            ModifiableMoOnTree.Assistant assistant = new ModifiableMoOnTree.Assistant() {
                int block = 500;
                int[] cnts1 = new int[block];
                int[] cnts2 = new int[block * block];
                long[] sum = new long[100000 + 1];

                @Override
                public void apply(ModifiableMoOnTree.Modification m) {
                    status[m.x] = m.to;
                }

                @Override
                public void revoke(ModifiableMoOnTree.Modification m) {
                    status[m.x] = m.from;
                }

                @Override
                public void add(int i) {
                    i = status[i];
                    int origin = dm.iThElement(i);
                    cnts1[i / block]++;
                    cnts2[i]++;
                    sum[cOf(origin)] += wOf(origin);
                    //debug.debug("" + cOf(origin), "+" + wOf(origin));
                }

                @Override
                public void remove(int i) {
                    i = status[i];
                    int origin = dm.iThElement(i);
                    cnts1[i / block]--;
                    cnts2[i]--;
                    sum[cOf(origin)] -= wOf(origin);
                    //debug.debug("" + cOf(origin), "-" + wOf(origin));
                }

                @Override
                public void query(ModifiableMoOnTree.Query q) {
                    if (q.t == 0) {
                        q.answer = sum[q.c];
                        return;
                    }

                    int r = dm.floorRankOf(statusOf(10000, q.c));
                    for (; (r + 1) % block != 0; r--) {
                        if (cnts2[r] != 0) {
                            q.answer = wOf(dm.iThElement(r));
                            return;
                        }
                    }
                    for (; cnts1[r / block] == 0; r -= block) ;

                    for (; ; r--) {
                        if (cnts2[r] != 0) {
                            q.answer = wOf(dm.iThElement(r));
                            return;
                        }
                    }
                }
            };

            mo.solve(qList.toArray(new ModifiableMoOnTree.Query[0]),
                    mList.toArray(new ModifiableMoOnTree.Modification[0]),
                    assistant,
                    mList.size());

            for (ModifiableMoOnTree.Query query : qList) {
                io.cache.append(query.answer).append('\n');
            }
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

        /**
         * Return 0, 1, so on
         */
        public int rankOf(int x) {
            return Arrays.binarySearch(val, f, t, x) - f;
        }

        public int floorRankOf(int x) {
            int index = Arrays.binarySearch(val, f, t, x);
            if (index >= 0) {
                return index - f;
            }
            index = -(index + 1);
            return index - 1 - f;
        }

        public int ceilRankOf(int x) {
            int index = Arrays.binarySearch(val, f, t, x);
            if (index >= 0) {
                return index - f;
            }
            index = -(index + 1);
            return index - f;
        }

        /**
         * Get the i-th smallest element
         */
        public int iThElement(int i) {
            return val[f + i];
        }

        public int minRank() {
            return 0;
        }

        public int maxRank() {
            return t - f - 1;
        }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOfRange(val, f, t));
        }
    }


    /**
     * Created by dalt on 2018/6/1.
     */
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


    /**
     * Created by dalt on 2018/5/20.
     */
    public static class St<T> {
        //st[i][j] means the min value between [i, i + 2^j),
        //so st[i][j] equals to min(st[i][j - 1], st[i + 2^(j - 1)][j - 1])
        Object[][] st;
        Comparator<T> comparator;

        int[] floorLogTable;

        public St(Object[] data, int length, Comparator<T> comparator) {
            int m = floorLog2(length);
            st = new Object[m + 1][length];
            this.comparator = comparator;
            for (int i = 0; i < length; i++) {
                st[0][i] = data[i];
            }
            for (int i = 0; i < m; i++) {
                int interval = 1 << i;
                for (int j = 0; j < length; j++) {
                    if (j + interval < length) {
                        st[i + 1][j] = min((T) st[i][j], (T) st[i][j + interval]);
                    } else {
                        st[i + 1][j] = st[i][j];
                    }
                }
            }

            floorLogTable = new int[length + 1];
            int log = 1;
            for (int i = 0; i <= length; i++) {
                if ((1 << log) <= i) {
                    log++;
                }
                floorLogTable[i] = log - 1;
            }
        }

        public static int floorLog2(int x) {
            return 31 - Integer.numberOfLeadingZeros(x);
        }

        private T min(T a, T b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }

        public static int ceilLog2(int x) {
            return 32 - Integer.numberOfLeadingZeros(x - 1);
        }

        /**
         * query the min value in [left,right]
         */
        public T query(int left, int right) {
            int queryLen = right - left + 1;
            int bit = floorLogTable[queryLen];
            //x + 2^bit == right + 1
            //So x should be right + 1 - 2^bit - left=queryLen - 2^bit
            return min((T) st[bit][left], (T) st[bit][right + 1 - (1 << bit)]);
        }
    }

    public static class ModifiableMoOnTree {
        private static class Node {
            List<Node> next = new ArrayList(2);
            int id;
            int close;
            int open;
            boolean added;
            int dfn;

            @Override
            public String toString() {
                return "" + id;
            }
        }

        Node[] nodes;

        public ModifiableMoOnTree(int n) {
            nodes = new Node[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }
        }

        public void addEdge(int a, int b) {
            nodes[a].next.add(nodes[b]);
            nodes[b].next.add(nodes[a]);
        }

        private boolean preHandled = false;

        private void preHandle() {
            if (preHandled) {
                return;
            }
            preHandled = true;
            eulerTrace = new Node[nodes.length * 2];
            lcaTrace = new Node[nodes.length * 2 - 1];
            dfs(nodes[0], null);
            st = new St(lcaTrace, lcaTraceTail, new Comparator<Node>() {
                @Override
                public int compare(Node a, Node b) {
                    return a.dfn - b.dfn;
                }
            });
        }

        Node[] eulerTrace;
        int eulerTraceTail = 0;
        Node[] lcaTrace;
        int lcaTraceTail = 0;
        St<Node> st;

        private void dfs(Node root, Node father) {
            root.open = eulerTraceTail;
            eulerTrace[eulerTraceTail++] = root;
            root.dfn = lcaTraceTail;
            lcaTrace[lcaTraceTail++] = root;
            for (Node node : root.next) {
                if (node == father) {
                    continue;
                }
                dfs(node, root);
                lcaTrace[lcaTraceTail++] = root;
            }
            root.close = eulerTraceTail;
            eulerTrace[eulerTraceTail++] = root;
        }

        public void solve(Query[] queries, Modification[] modifications, Assistant assistant, int now) {
            preHandle();

            final int blockSize = Math.max((int) Math.pow(nodes.length, 2.0 / 3), 1);

            for (Query q : queries) {
                Node u = nodes[q.u];
                Node v = nodes[q.v];
                if (u.open > v.open) {
                    Node tmp = u;
                    u = v;
                    v = tmp;
                }
                if (u.close <= v.open) {
                    q.l = u.close;
                    q.r = v.open;
                } else {
                    q.l = v.close;
                    q.r = u.close - 1;
                }
                q.lca = st.query(Math.min(u.dfn, v.dfn), Math.max(u.dfn, v.dfn)).id;
            }

            Arrays.sort(queries, new Comparator<Query>() {
                @Override
                public int compare(Query a, Query b) {
                    int r = a.l / blockSize - b.l / blockSize;
                    if (r == 0) {
                        r = a.version / blockSize - b.version / blockSize;
                    }
                    if (r == 0) {
                        r = a.r - b.r;
                    }
                    return r;
                }
            });

            int l = 0;
            int r = -1;
            for (Node node : nodes) {
                node.added = false;
            }


            for (Query q : queries) {
                while (now < q.version) {
                    Modification m = modifications[now];
                    Node x = nodes[m.x];
                    if (x.added) {
                        assistant.remove(x.id);
                    }
                    assistant.apply(m);
                    if (x.added) {
                        assistant.add(x.id);
                    }
                    now++;
                }
                while (now > q.version) {
                    now--;
                    Modification m = modifications[now];
                    Node x = nodes[m.x];
                    if (x.added) {
                        assistant.remove(x.id);
                    }
                    assistant.revoke(m);
                    if (x.added) {
                        assistant.add(x.id);
                    }
                }
                while (r < q.r) {
                    r++;
                    Node x = eulerTrace[r];
                    if (x.added) {
                        assistant.remove(x.id);
                    } else {
                        assistant.add(x.id);
                    }
                    x.added = !x.added;
                }
                while (l > q.l) {
                    l--;
                    Node x = eulerTrace[l];
                    if (x.added) {
                        assistant.remove(x.id);
                    } else {
                        assistant.add(x.id);
                    }
                    x.added = !x.added;
                }
                while (r > q.r) {
                    Node x = eulerTrace[r];
                    if (x.added) {
                        assistant.remove(x.id);
                    } else {
                        assistant.add(x.id);
                    }
                    x.added = !x.added;
                    r--;
                }
                while (l < q.l) {
                    Node x = eulerTrace[l];
                    if (x.added) {
                        assistant.remove(x.id);
                    } else {
                        assistant.add(x.id);
                    }
                    x.added = !x.added;
                    l++;
                }

                Node lca = nodes[q.lca];
                if (lca.added) {
                    assistant.remove(q.lca);
                } else {
                    assistant.add(q.lca);
                }
                lca.added = !lca.added;
                assistant.query(q);
                if (lca.added) {
                    assistant.remove(q.lca);
                } else {
                    assistant.add(q.lca);
                }
                lca.added = !lca.added;
            }

        }

        public static class Query {
            int l;
            int r;
            int version;
            int u;
            int v;
            long answer;
            int lca;
            int t; //0 means sum, 1 for maximum
            int c;

            @Override
            public String toString() {
                return "(" + u + "," + v + ")[" + version + "]";
            }
        }

        public static class Modification {
            int x;
            int from;
            int to;

            @Override
            public String toString() {
                return x + "[" + from + "->" + to + "]";
            }
        }

        public interface Assistant {
            void apply(Modification m);

            void revoke(Modification m);

            void add(int i);

            void remove(int i);

            void query(Query q);
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
}
