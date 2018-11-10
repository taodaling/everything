package com.daltao.oj.old.submit.codeforces;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class CF986E {
    static final int INF = (int) 1e8;
    static final int MOD = (int) 1e9 + 7;
    public static IOUtil io;
    public static Debug debug;

    public static void main(String[] args) throws FileNotFoundException {
        before();
        new Task().run();
        after();
    }

    public static void before() throws FileNotFoundException {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            io = new IOUtil(new FileInputStream("E:\\DATABASE\\TESTCASE\\codeforces\\CF986E.in"), System.out);
        } else {
            io = new IOUtil(System.in, System.out);
        }

        debug = new Debug();
        debug.enter("main");
    }

    public static void after() {
        io.flush();
        debug.exit();
        debug.statistic();
    }

    public static class Task implements Runnable {
        int[] primes = new int[1000000];
        int[] factors = new int[10000001];
        static int MIN = 1;
        static int MAX = (int) 1e7;

        @Override
        public void run() {
            eulerSieve(10000000, primes, factors);

            int n = io.readInt();

            Node[] nodes = new Node[n + 1];
            for (int i = 0; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }

            nodes[0].children.add(nodes[1]);
            for (int i = 1; i < n; i++) {
                Node u = nodes[io.readInt()];
                Node v = nodes[io.readInt()];
                u.children.add(v);
                v.children.add(u);
            }

            for (int i = 1; i <= n; i++) {
                nodes[i].num = io.readInt();
            }

            AutoArray<Node> trace = new AutoArray<Node>(new Node[2 * n + 2]);
            dfs(nodes[0], null, trace);
            St<Node> st = new St<Node>(trace.toArray(), trace.size(), new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return o1.dfn - o2.dfn;
                }
            });

            nodes[0].segment = Segment.build(MIN, MAX);
            for (int i = 1, until = trace.size(); i < until; i++) {
                Node node = trace.data[i];
                if (node.segment != null) {
                    continue;
                }
                node.segment = node.father.segment;
                int val = node.num;
                while (val != 1) {
                    int factor = factors[val];
                    int pro = 1;
                    while (val % factor == 0) {
                        val /= factor;
                        pro *= factor;
                        node.segment = Segment.updatePersistently(pro, pro, MIN, MAX, 1, node.segment);
                    }
                }
            }

            int q = io.readInt();
            for (int i = 0; i < q; i++) {
                Node u = nodes[io.readInt()];
                Node v = nodes[io.readInt()];
                int x = io.readInt();

                Node lca = st.query(Math.min(u.dfn, v.dfn), Math.max(u.dfn, v.dfn));

                long product = 1;
                while (x != 1) {
                    int factor = factors[x];
                    int pro = 1;
                    while (x % factor == 0) {
                        x /= factor;
                        pro *= factor;
                    }
                    int appearTimes = 0;
                    while (pro > 1) {
                        int time = Segment.query(pro, pro, MIN, MAX, u.segment)
                                + Segment.query(pro, pro, MIN, MAX, v.segment)
                                - Segment.query(pro, pro, MIN, MAX, lca.segment) * 2
                                + (lca.num % pro == 0 ? 1 : 0);
                        time -= appearTimes;
                        appearTimes += time;

                        product = (product * Mathematics.pow(pro, time, MOD)) % MOD;
                        pro /= factor;
                    }
                }

                io.write(product);
                io.write('\n');
            }
        }

        public static void dfs(Node node, Node father, AutoArray<Node> trace) {
            node.dfn = trace.size();
            node.father = father;
            trace.push(node);
            for (Node child : node.children) {
                if (child == father) {
                    continue;
                }
                dfs(child, node, trace);
                trace.push(node);
            }
        }

        public static int eulerSieve(int limit, int[] primes, int[] factors) {
            boolean[] isComp = new boolean[limit + 1];
            int wpos = 0;
            factors[1] = 1;
            for (int i = 2; i <= limit; i++) {
                if (!isComp[i]) {
                    primes[wpos++] = i;
                    factors[i] = i;
                }
                for (int j = 0, until = limit / i; j < wpos && primes[j] <= until; j++) {
                    int pi = primes[j] * i;
                    isComp[pi] = true;
                    factors[pi] = primes[j];
                    if (i % primes[j] == 0) {
                        break;
                    }
                }
            }
            return wpos;
        }
    }

    public static class Node {
        List<Node> children = new ArrayList<>(1);
        Node father;
        int dfn;
        int id;
        int num;
        Segment segment;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class AutoArray<T> {
        T[] data;
        int size;

        public AutoArray(T[] data) {
            this.data = data;
        }

        public T[] toArray() {
            return data;
        }

        public int size() {
            return size;
        }

        public void push(T v) {
            data[size++] = v;
        }

        public T pop() {
            return data[--size];
        }
    }

    public static class St<T> {
        //st[i][j] means the min value between [i, i + 2^j),
        //so st[i][j] equals to min(st[i][j - 1], st[i + 2^(j - 1)][j - 1])
        Object[][] st;
        Comparator<T> comparator;

        int[] floorLogTable;

        public St(Object[] data, int length, Comparator<T> comparator) {
            int m = floorLog2(length);
            st = new Object[length][m + 1];
            this.comparator = comparator;
            for (int i = 0; i < length; i++) {
                st[i][0] = data[i];
            }
            for (int i = 0; i < m; i++) {
                int interval = 1 << i;
                for (int j = 0; j < length; j++) {
                    if (j + interval < length) {
                        st[j][i + 1] = min((T) st[j][i], (T) st[j + interval][i]);
                    } else {
                        st[j][i + 1] = st[j][i];
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

        /**
         * query the min value in [left,right]
         */
        public T query(int left, int right) {
            int queryLen = right - left + 1;
            int bit = floorLogTable[queryLen];
            //x + 2^bit == right + 1
            //So x should be right + 1 - 2^bit - left=queryLen - 2^bit
            return min((T) st[left][bit], (T) st[right + 1 - (1 << bit)][bit]);
        }
    }

    public static class Mathematics {
        /**
         * Get the greatest common divisor of a and b
         */
        public static int gcd(int a, int b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private static int gcd0(int a, int b) {
            return b == 0 ? a : gcd0(b, a % b);
        }

        /**
         * Get the greatest common divisor of a and b
         */
        public static long gcd(long a, long b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private static long gcd0(long a, long b) {
            return b == 0 ? a : gcd0(b, a % b);
        }

        /**
         * Get y where x * y = 1 (% mod)
         */
        public static int inverse(int x, int mod) {
            return pow(x, mod - 2, mod);
        }

        /**
         * Get x^n(% mod)
         */
        public static int pow(int x, int n, int mod) {
            n = mod(n, mod - 1);
            x = mod(x, mod);
            int bit = 31 - Integer.numberOfLeadingZeros(n);
            long product = 1;
            for (; bit >= 0; bit--) {
                product = product * product % mod;
                if (((1 << bit) & n) != 0) {
                    product = product * x % mod;
                }
            }
            return (int) product;
        }

        /**
         * Get x % mod
         */
        public static int mod(int x, int mod) {
            return x >= 0 ? x % mod : (((x % mod) + mod) % mod);
        }

        /**
         * Get (x1, y1)·(x2, y2)
         */
        public static long cross(int x1, int y1, int x2, int y2) {
            return (long) x1 * y2 - (long) y1 * x2;
        }

        /**
         * Get (x1, y1)·(x2, y2)
         */
        public static double cross(double x1, double y1, double x2, double y2) {
            return x1 * y2 - y1 * x2;
        }

        /**
         * Get n!/(n-m)!
         */
        public static long permute(int n, int m) {
            return m == 0 ? 1 : n * permute(n - 1, m - 1);
        }

        /**
         * Put all primes less or equal to limit into primes after offset
         */
        public static int eulerSieve(int limit, int[] primes, int offset) {
            boolean[] isComp = new boolean[limit + 1];
            int wpos = offset;
            for (int i = 2; i <= limit; i++) {
                if (!isComp[i]) {
                    primes[wpos++] = i;
                }
                for (int j = offset, until = limit / i; j < wpos && primes[j] <= until; j++) {
                    int pi = primes[j] * i;
                    isComp[pi] = true;
                    if (i % primes[j] == 0) {
                        break;
                    }
                }
            }
            return wpos - offset;
        }

        /**
         * Round x into integer
         */
        public static int intRound(double x) {
            return (int) (x + 0.5);
        }

        /**
         * Round x into long
         */
        public static long longRound(double x) {
            return (long) (x + 0.5);
        }
    }

    public static class Segment implements Cloneable {
        Segment left;
        Segment right;
        int val;

        public static Segment build(int l, int r) {
            return new Segment();
        }

        public static void update(int f, int t, int l, int r, Segment segment) {
            if (f > r || t < l) {
                return;
            }
            if (f <= l && r <= t) {
                return;
            }
            int m = (l + r) >> 1;

            segment.pushDown();
            update(f, t, l, m, segment.left);
            update(f, t, m + 1, r, segment.right);
            segment.pushUp();
        }

        public static Segment updatePersistently(int f, int t, int l, int r, int val, Segment segment) {
            if (f > r || t < l) {
                return segment;
            }
            segment = segment.clone();
            if (f <= l && r <= t) {
                segment.val += val;
                return segment;
            }
            int m = (l + r) >> 1;

            segment.pushDown();
            segment.left = updatePersistently(f, t, l, m, val, segment.left);
            segment.right = updatePersistently(f, t, m + 1, r, val, segment.right);
            segment.pushUp();
            return segment;
        }

        public static int query(int f, int t, int l, int r, Segment segment) {
            if (f > r || t < l) {
                return 0;
            }
            if (f <= l && r <= t) {
                return segment.val;
            }
            int m = (l + r) >> 1;

            segment.pushDown();
            return query(f, t, l, m, segment.left) +
                    query(f, t, m + 1, r, segment.right);
        }

        public void pushDown() {
            if (left == null) {
                left = new Segment();
                right = new Segment();
            }
        }

        public void pushUp() {
        }

        @Override
        public Segment clone() {
            try {
                return (Segment) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Debug {
        boolean debug = System.getProperty("ONLINE_JUDGE") == null;
        Deque<ModuleRecorder> stack = new ArrayDeque<>();
        Map<String, Module> fragmentMap = new HashMap<>();

        public void enter(String module) {
            if (debug) {
                stack.push(new ModuleRecorder(getModule(module)));
            }
        }

        public Module getModule(String moduleName) {
            Module module = fragmentMap.get(moduleName);
            if (module == null) {
                module = new Module(moduleName);
                fragmentMap.put(moduleName, module);
            }
            return module;
        }

        public void exit() {
            if (debug) {
                ModuleRecorder fragment = stack.pop();
                fragment.exit();
            }
        }

        public void statistic() {
            if (!debug) {
                return;
            }

            if (stack.size() > 0) {
                throw new RuntimeException("Exist unexited tag");
            }
            System.out.println("\n------------------------------------------");

            System.out.println("memory used " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + "M");

            System.out.println("\n------------------------------------------");
            for (Module module : fragmentMap.values()) {
                System.out.println(String.format("Module %s : enter %d : cost %d", module.moduleName, module.enterTime, module.totaltime));
            }

            System.out.println("------------------------------------------");
        }

        public static class ModuleRecorder {
            Module fragment;
            long time;

            public ModuleRecorder(Module fragment) {
                this.fragment = fragment;
                time = System.currentTimeMillis();
            }

            public void exit() {
                fragment.totaltime += System.currentTimeMillis() - time;
                fragment.enterTime++;
            }
        }

        public static class Module {
            String moduleName;
            long totaltime;
            long enterTime;

            public Module(String moduleName) {
                this.moduleName = moduleName;
            }
        }
    }

    public static class IOUtil {
        private static int BUF_SIZE = 1 << 13;

        private byte[] r_buf = new byte[BUF_SIZE];
        private int r_cur;
        private int r_total;
        private int r_next;
        private final InputStream in;

        StringBuilder w_buf = new StringBuilder();
        private final OutputStream out;

        public IOUtil(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        private void skipBlank() {
            while (r_next <= 32) {
                r_next = read();
            }
        }

        public int readString(char[] data, int offset, int limit) {
            skipBlank();

            int originalLimit = limit;
            while (limit > 0 && r_next > 32) {
                data[offset++] = (char) r_next;
                limit--;
                r_next = read();
            }

            return originalLimit - limit;
        }

        public String readString(StringBuilder builder) {
            skipBlank();

            while (r_next > 32) {
                builder.append((char) r_next);
            }

            return builder.toString();
        }

        public String readString() {
            return readString(new StringBuilder(16));
        }

        public long readUnsignedLong() {
            skipBlank();

            long num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 + r_next - '0';
                r_next = read();
            }
            return num;
        }

        public long readLong() {
            skipBlank();

            int sign = 1;
            while (r_next == '+' || r_next == '-') {
                if (r_next == '-') {
                    sign *= -1;
                }
                r_next = read();
            }

            return sign * readUnsignedLong();
        }

        public int readUnsignedInt() {
            skipBlank();

            int num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 + r_next - '0';
                r_next = read();
            }
            return num;
        }

        public int readInt() {
            skipBlank();

            int sign = 1;
            while (r_next == '+' || r_next == '-') {
                if (r_next == '-') {
                    sign *= -1;
                }
                r_next = read();
            }

            return sign * readUnsignedInt();
        }

        public int read() {
            while (r_total <= r_cur) {
                try {
                    r_total = in.read(r_buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                r_cur = 0;
                if (r_total == -1) {
                    return -1;
                }
            }
            return r_buf[r_cur++];
        }

        public void write(char c) {
            w_buf.append(c);
        }

        public void write(int n) {
            w_buf.append(n);
        }

        public void write(String s) {
            w_buf.append(s);
        }

        public void write(long s) {
            w_buf.append(s);
        }

        public void write(double s) {
            w_buf.append(s);
        }

        public void write(float s) {
            w_buf.append(s);
        }

        public void write(Object s) {
            w_buf.append(s);
        }

        public void write(char[] data, int offset, int cnt) {
            for (int i = offset, until = offset + cnt; i < until; i++) {
                write(data[i]);
            }
        }

        public void flush() {
            try {
                out.write(w_buf.toString().getBytes(Charset.forName("ascii")));
                w_buf.setLength(0);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
