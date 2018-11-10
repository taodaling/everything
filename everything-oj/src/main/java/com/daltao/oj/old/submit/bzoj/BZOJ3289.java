package com.daltao.oj.old.submit.bzoj;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BZOJ3289 {
    public static void main(String[] args) throws FileNotFoundException {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        IOUtil io;
        if (local) {
            io = new IOUtil(new FileInputStream("E:\\DATABASE\\TESTCASE\\BZOJ3289.in"), new FileOutputStream("E:\\DATABASE\\TESTCASE\\BZOJ3289.out"));
        } else {
            io = new IOUtil(System.in, System.out);
        }

        Task task = new Task(io);
        task.run();
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

            Integer[] files = new Integer[n + 1];
            files[0] = 0;
            for (int i = 1; i <= n; i++) {
                files[i] = io.readInt();
            }

            CompressedDictionary<Integer> dictionary = new CompressedDictionary(files, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });

            for (int i = 0; i <= n; i++) {
                files[i] = dictionary.query(files[i]);
            }

            int m = io.readInt();
            QueryImpl[] queries = new QueryImpl[m];
            for (int i = 0; i < m; i++) {
                queries[i] = new QueryImpl(io.readInt(), io.readInt());
            }

            MDAlgorithm<Integer> md = new MDAlgorithm();
            md.setData(files);
            md.setQueries(queries);
            md.setInterval(new IntervalImpl());

            md.solve();

            for (int i = 0; i < m; i++) {
                io.write(queries[i].answer);
                io.write('\n');
            }
        }
    }


    public static class QueryImpl implements MDAlgorithm.Queries<Integer> {
        final int l;
        final int r;
        int answer;

        public QueryImpl(int l, int r) {
            this.l = l;
            this.r = r;
        }

        @Override
        public void setAnswer(MDAlgorithm.Interval<Integer> interval) {
            answer = ((IntervalImpl) interval).cost;
        }

        @Override
        public int getLeft() {
            return l;
        }

        @Override
        public int getRight() {
            return r;
        }
    }

    public static class IntervalImpl implements MDAlgorithm.Interval<Integer> {
        static final int MAX_VALUE = 50000;
        BIT bit = new BIT(MAX_VALUE);
        int cost = 0;

        @Override
        public void llMove(Integer add) {
            cost += bit.query(add - 1);
            bit.update(add, 1);
        }

        @Override
        public void rrMove(Integer add) {
            cost += bit.query(MAX_VALUE) - bit.query(add);
            bit.update(add, 1);
        }

        @Override
        public void lrMove(Integer remove) {
            bit.update(remove, -1);
            cost -= bit.query(remove - 1);
        }

        @Override
        public void rlMove(Integer remove) {
            bit.update(remove, -1);
            cost -= bit.query(MAX_VALUE) - bit.query(remove);
        }

        @Override
        public void clear() {

        }
    }

    public static class CompressedDictionary<T> {
        Map<T, Integer> map;

        public CompressedDictionary(T[] data, Comparator<T> cmp) {
            data = data.clone();
            Arrays.sort(data, cmp);
            int n = data.length;

            map = new HashMap(n);
            map.put(data[0], 0);
            for (int i = 1; i < n; i++) {
                if (cmp.compare(data[i], data[i - 1]) == 0) {
                    map.put(data[i], map.size() - 1);
                } else {
                    map.put(data[i], map.size());
                }
            }
        }

        public Integer query(T val) {
            return map.get(val);
        }
    }

    public static class BIT {
        private int[] data;
        private int n;

        /**
         * 创建大小A[1...n]
         */
        public BIT(int n) {
            this.n = n;
            data = new int[n + 1];
        }

        /**
         * 查询A[1]+A[2]+...+A[i]
         */
        public int query(int i) {
            int sum = 0;
            for (; i > 0; i -= i & -i) {
                sum += data[i];
            }
            return sum;
        }

        /**
         * 将A[i]更新为A[i]+mod
         */
        public void update(int i, int mod) {
            for (; i <= n; i += i & -i) {
                data[i] += mod;
            }
        }
    }

    public static class MDAlgorithm<T> {

        public static interface Queries<T> {
            void setAnswer(Interval<T> interval);

            int getLeft();

            int getRight();
        }

        public static interface Interval<T> {
            void llMove(T add);

            void rrMove(T add);

            void lrMove(T remove);

            void rlMove(T remove);

            void clear();
        }

        public void setData(T[] data) {
            this.data = data;
        }

        public void setQueries(Queries<T>[] queries) {
            this.queries = queries.clone();
        }

        public void setInterval(Interval<T> interval) {
            this.interval = interval;
        }

        public void solve() {
            int n = data.length;
            int m = queries.length;

            if (n == 0 || m == 0) {
                return;
            }

            final int k = Math.max(1, Mathematics.intRound(n / Math.sqrt(m)));

            Arrays.sort(queries, new Comparator<Queries<T>>() {
                @Override
                public int compare(Queries<T> o1, Queries<T> o2) {
                    int c = o1.getLeft() / k - o2.getLeft() / k;
                    if (c == 0) {
                        c = o1.getRight() - o2.getRight();
                    }
                    return c;
                }
            });

            interval.clear();
            int left = queries[0].getLeft();
            int right = left - 1;
            for (int i = 0; i < m; i++) {
                Queries<T> q = queries[i];
                int l = q.getLeft();
                int r = q.getRight();

                while (left > l) {
                    interval.llMove(data[--left]);
                }

                while (right < r) {
                    interval.rrMove(data[++right]);
                }

                while (left < l) {
                    interval.lrMove(data[left++]);
                }

                while (right > r) {
                    interval.rlMove(data[right--]);
                }

                q.setAnswer(interval);
            }

            return;
        }

        T[] data;
        Queries<T>[] queries;
        Interval<T> interval;
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
