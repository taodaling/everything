package com.daltao.oj.old.submit.spoj;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class SPOJCOT2 {
    public static void main(String[] args) throws FileNotFoundException {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        IOUtil io;
        long begin = 0;
        long end = 0;
        if (local) {
            io = new IOUtil(new FileInputStream("E:\\DATABASE\\TESTCASE\\SPOJCOT2.in"), new FileOutputStream("E:\\DATABASE\\TESTCASE\\SPOJCOT2.out"));
            begin = System.currentTimeMillis();
        } else {
            io = new IOUtil(System.in, System.out);
        }

        try {
            Task task = new Task(io);
            task.run();
        } catch (StackOverflowError e) {
            while (true) ;
        }

        if (local) {
            end = System.currentTimeMillis();
            io.write(String.format("\ntime:%d\n", end - begin));
            io.write(String.format("memory:%d\n", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        }

        io.flush();
    }

    public static class Task implements Runnable {
        IOUtil io;

        public Task(IOUtil io) {
            this.io = io;
        }

        @Override
        public void run() {
            int n = io.readInt();
            int m = io.readInt();

            Integer[] value = new Integer[n];
            for (int i = 0; i < n; i++) {
                value[i] = io.readInt();
            }

            CompressedDictionary<Integer> dict = new CompressedDictionary<>(value, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            }, false);

            for (int i = 0; i < n; i++) {
                value[i] = dict.query(value[i]);
            }

            TreeMOAlgorithm<Integer> mo = new TreeMOAlgorithm<>(n);
            for (int i = 0; i < n; i++) {
                mo.setNodeValue(i + 1, value[i]);
            }

            for (int i = 1; i < n; i++) {
                mo.buildEdge(io.readInt(), io.readInt());
            }

            mo.buildFinish();
            QueryImpl[] queries = new QueryImpl[m];
            for (int i = 0; i < m; i++) {
                queries[i] = new QueryImpl(io.readInt(), io.readInt());
            }

            mo.solve(queries, new IntervalImpl());

            for (int i = 0; i < m; i++) {
                io.write(queries[i].ans);
                io.write('\n');
            }
        }
    }

    public static class CompressedDictionary<T> {
        Map<T, Integer> map;
        Map<Integer, T> rev;

        public CompressedDictionary(T[] data, Comparator<T> cmp, boolean needReverse) {
            data = data.clone();
            cmp = cmp;

            Arrays.sort(data, cmp);
            int n = data.length;

            map = new HashMap<>(n);
            map.put(data[0], 0);
            for (int i = 1; i < n; i++) {
                if (cmp.compare(data[i], data[i - 1]) == 0) {
                    map.put(data[i], map.size() - 1);
                } else {
                    map.put(data[i], map.size());
                }
            }

            if (needReverse) {
                rev = new HashMap<>(n);
                for (Map.Entry<T, Integer> entry : map.entrySet()) {
                    rev.put(entry.getValue(), entry.getKey());
                }
            }
        }

        public Integer query(T val) {
            return map.get(val);
        }

        public T reverse(Integer val) {
            return rev.get(val);
        }
    }

    public static class QueryImpl implements TreeMOAlgorithm.OutsideQuery {
        final int u;
        final int v;
        int ans;

        public QueryImpl(int u, int v) {
            this.u = u;
            this.v = v;
        }

        @Override
        public int getU() {
            return u;
        }

        @Override
        public int getV() {
            return v;
        }

        @Override
        public void setAnswer(TreeMOAlgorithm.Interval interval) {
            ans = ((IntervalImpl) interval).type;
        }
    }

    public static class IntervalImpl implements TreeMOAlgorithm.Interval<Integer> {
        static final int MAX = (int) 4e4;
        int[] cnt = new int[MAX];
        int type = 0;

        @Override
        public void add(Integer val) {
            if (cnt[val] == 0) {
                type++;
            }
            cnt[val]++;
        }

        @Override
        public void remove(Integer val) {
            cnt[val]--;
            if (cnt[val] == 0) {
                type--;
            }
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
            return min((T) st[left][bit], (T) st[right + 1 - (1 << bit)][bit]);
        }
    }

    public static class TreeMOAlgorithm<T> {
        public static class Node<T> {
            List<Node> children = new ArrayList<>(1);
            int dfn1;
            int dfn2;
            int order;
            int id;
            T value;
        }

        public static interface OutsideQuery {
            public int getU();

            public int getV();

            public void setAnswer(Interval interval);
        }

        public static interface Interval<T> {
            void add(T val);

            void remove(T val);

        }

        public static class InnerQuery<T> {
            OutsideQuery outsideQuery;
            int extra = -1;
            int left;
            int right;

            public void setAnswer(Interval interval) {
                outsideQuery.setAnswer(interval);
            }

            public int getLeft() {
                return left;
            }

            public int getRight() {
                return right;
            }

            public int getExtra() {
                return extra;
            }
        }

        Node[] nodes;
        int nodeNum;
        St<Node<T>> st;

        public TreeMOAlgorithm(int nodeNum) {
            this.nodeNum = nodeNum;
            nodes = new Node[nodeNum + 1];
            for (int i = 1; i <= nodeNum; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }
            in = new boolean[nodeNum + 1];
        }

        public void buildEdge(int u, int v) {
            nodes[u].children.add(nodes[v]);
            nodes[v].children.add(nodes[u]);
        }

        public void setNodeValue(int i, T val) {
            nodes[i].value = val;
        }

        Object[] values;
        int[] sequence;
        boolean[] in;

        private static <T> void flat(Node<T> root, Node<T> father, List<T> list, List<Node<T>> trace) {
            root.dfn1 = list.size();
            list.add(root.value);
            root.order = trace.size();
            trace.add(root);
            for (Node child : root.children) {
                if (child == father) {
                    continue;
                }
                flat(child, root, list, trace);
                trace.add(root);
            }
            root.dfn2 = list.size();
            list.add(root.value);
        }

        public void buildFinish() {
            List<T> list = new ArrayList<>(nodeNum * 2);
            List<Node<T>> trace = new ArrayList<>(nodeNum * 2);
            flat(nodes[1], null, list, trace);
            values = list.toArray();
            sequence = new int[nodeNum * 2];
            for (int i = 1; i <= nodeNum; i++) {
                sequence[nodes[i].dfn1] = sequence[nodes[i].dfn2] = nodes[i].id;
            }
            st = new St<>(trace.toArray(), trace.size(), new Comparator<Node<T>>() {
                @Override
                public int compare(Node<T> o1, Node<T> o2) {
                    return Integer.compare(o1.order, o2.order);
                }
            });
        }

        Interval<T> interval;

        public void solve(OutsideQuery[] outsideQueries, Interval<T> interval) {
            int n = values.length;
            int q = outsideQueries.length;
            if (nodeNum == 0 || q == 0) {
                return;
            }
            this.interval = interval;

            InnerQuery<T>[] innerQueries = new InnerQuery[q];

        /*for (int i = 0, until = values.length; i < until; i++) {
            nodes[sequence[i]].value = values[i];
        }*/

            for (int i = 0; i < q; i++) {
                innerQueries[i] = new InnerQuery();
                innerQueries[i].outsideQuery = outsideQueries[i];

                Node u = nodes[outsideQueries[i].getU()];
                Node v = nodes[outsideQueries[i].getV()];
                if (u.dfn1 > v.dfn1) {
                    Node tmp = u;
                    u = v;
                    v = tmp;
                }
                //u is lca(u,v)
                if (u.dfn2 >= v.dfn1) {
                    innerQueries[i].left = v.dfn2;
                    innerQueries[i].right = u.dfn2;
                } else {
                    innerQueries[i].left = u.dfn2;
                    innerQueries[i].right = v.dfn1;
                    innerQueries[i].extra = st.query(u.order, v.order).dfn1;
                }
            }


            int k = Math.max(1, Mathematics.intRound(n / Math.sqrt(q)));

            Arrays.sort(innerQueries, new Comparator<InnerQuery<T>>() {
                @Override
                public int compare(InnerQuery<T> o1, InnerQuery<T> o2) {
                    int c = o1.getLeft() / k - o2.getLeft() / k;
                    if (c == 0) {
                        c = o1.getRight() - o2.getRight();
                    }
                    return c;
                }
            });

            int left = innerQueries[0].getLeft();
            int right = left - 1;

            Arrays.fill(in, false);
            for (int i = 0; i < q; i++) {
                InnerQuery<T> query = innerQueries[i];
                int l = query.getLeft();
                int r = query.getRight();

                while (left > l) {
                    add(--left);
                }

                while (right < r) {
                    add(++right);
                }

                while (left < l) {
                    remove(left++);
                }

                while (right > r) {
                    remove(left--);
                }

                if (query.extra != -1) {
                    interval.add((T) values[query.extra]);
                    query.setAnswer(interval);
                    interval.remove((T) values[query.extra]);
                } else {
                    query.setAnswer(interval);
                }
            }

            return;
        }

        public void add(int i) {
            int j = sequence[i];
            if (in[j]) {
                interval.remove((T) values[i]);
            } else {
                interval.add((T) values[i]);
            }
            in[j] = !in[j];
        }

        public void remove(int i) {
            add(i);
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

        public static int extgcd(int a, int b, int[] coe) {
            return a >= b ? extgcd0(a, b, coe) : extgcd0(b, a, coe);
        }

        private static int extgcd0(int a, int b, int[] coe) {
            if (b == 0) {
                coe[0] = 1;
                coe[1] = 0;
                return a;
            }
            int g = extgcd0(b, a % b, coe);
            int n = coe[0];
            int m = coe[1];
            coe[0] = m;
            coe[1] = n - m * (a / b);
            return g;
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

        public static long extgcd(long a, long b, long[] coe) {
            return a >= b ? extgcd0(a, b, coe) : extgcd0(b, a, coe);
        }

        private static long extgcd0(long a, long b, long[] coe) {
            if (b == 0) {
                coe[0] = 1;
                coe[1] = 0;
                return a;
            }
            long g = extgcd0(b, a % b, coe);
            long n = coe[0];
            long m = coe[1];
            coe[0] = m;
            coe[1] = n - m * (a / b);
            return g;
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
            if (x < 0) {
                return -(int) (-x + 0.5);
            }
            return (int) (x + 0.5);
        }

        /**
         * Round x into long
         */
        public static long longRound(double x) {
            if (x < 0) {
                return -(long) (-x + 0.5);
            }
            return (long) (x + 0.5);
        }
    }

    public static class IOUtil {
        private static int BUF_SIZE = 1 << 13;

        private byte[] r_buf = new byte[BUF_SIZE];
        private int r_cur;
        private int r_total;
        private int r_next;
        private final InputStream in;
        private StringBuilder temporary = new StringBuilder();

        StringBuilder w_buf = new StringBuilder();
        private final OutputStream out;

        public IOUtil(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        private void skipBlank() {
            while (r_next >= 0 && r_next <= 32) {
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
                r_next = read();
            }

            return builder.toString();
        }

        public String readString() {
            temporary.setLength(0);
            return readString(temporary);
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

            return sign == 1 ? readUnsignedLong() : readNegativeLong();
        }

        public long readNegativeLong() {
            skipBlank();

            long num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 - r_next + '0';
                r_next = read();
            }
            return num;
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

        public int readNegativeInt() {
            skipBlank();

            int num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 - r_next + '0';
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

            return sign == 1 ? readUnsignedInt() : readNegativeInt();
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

        public boolean hasMore() {
            skipBlank();
            return r_next != -1;
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

        public double readDouble() {
            return Double.parseDouble(readString());
        }
    }

    public static class Utils {
        public static <T> void swap(T[] data, int i, int j) {
            T tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static <T> int min(T[] data, int from, int to, Comparator<T> cmp) {
            int m = from;
            for (int i = from + 1; i < to; i++) {
                if (cmp.compare(data[m], data[i]) > 0) {
                    m = i;
                }
            }
            return m;
        }

        public static <T> void move(T[] data, int from, int to, int step) {
            int len = to - from;
            step = len - (step % len + len) % len;
            Object[] buf = new Object[len];
            for (int i = 0; i < len; i++) {
                buf[i] = data[(i + step) % len + from];
            }
            System.arraycopy(buf, 0, data, from, len);
        }
    }
}
