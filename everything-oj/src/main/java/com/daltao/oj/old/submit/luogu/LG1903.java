package com.daltao.oj.old.submit.luogu;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LG1903 {
    public static void main(String[] args) throws FileNotFoundException {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        IOUtil io;
        if (local) {
            io = new IOUtil(new FileInputStream("E:\\DATABASE\\TESTCASE\\LG1903.in"), new FileOutputStream("E:\\DATABASE\\TESTCASE\\LG1903.out"));
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
            int m = io.readInt();
            Integer[] board = new Integer[n + 1];
            for (int i = 1; i <= n; i++) {
                board[i] = io.readInt();
            }
            Integer[] copy = board.clone();

            int v = 0;
            List<ModificationImpl> modificationList = new ArrayList<>(m);
            List<QueryImpl> queryList = new ArrayList<>(m);
            char[] cmd = new char[1];
            for (int i = 0; i < m; i++) {
                io.readString(cmd, 0, 1);
                int l = io.readInt();
                int r = io.readInt();
                if (cmd[0] == 'Q') {
                    queryList.add(new QueryImpl(l, r, v));
                } else {
                    modificationList.add(new ModificationImpl(l, copy[l], r));
                    copy[l] = r;
                    v++;
                }
            }

            ModifiableMOAlgorithm<Integer> mo = new ModifiableMOAlgorithm<>();
            mo.setData(board);
            mo.setInterval(new IntervalImpl());
            mo.setModifications(modificationList.toArray(new ModificationImpl[0]));
            mo.setQueries(queryList.toArray(new QueryImpl[0]));
            mo.solve();

            for (QueryImpl q : queryList) {
                io.write(q.answer);
                io.write('\n');
            }
        }
    }

    public static class ModificationImpl implements ModifiableMOAlgorithm.Modification<Integer> {
        final int index;
        final Integer origin;
        final Integer replacement;

        public ModificationImpl(int index, int origin, int replacement) {
            this.index = index;
            this.origin = origin;
            this.replacement = replacement;
        }

        @Override
        public int index() {
            return index;
        }

        @Override
        public Integer invoke() {
            return replacement;
        }

        @Override
        public Integer revoke() {
            return origin;
        }
    }

    public static class IntervalImpl implements ModifiableMOAlgorithm.Interval<Integer> {
        static final int MAX = 1000000;
        int[] cnt = new int[MAX + 1];
        int noneZeroNumber;

        @Override
        public void add(Integer add) {
            cnt[add]++;
            if (cnt[add] == 1) {
                noneZeroNumber++;
            }
        }

        @Override
        public void remove(Integer remove) {
            cnt[remove]--;
            if (cnt[remove] == 0) {
                noneZeroNumber--;
            }
        }

        @Override
        public void clear() {

        }
    }

    public static class QueryImpl implements ModifiableMOAlgorithm.VersionQueries<Integer> {
        int answer;
        final int l;
        final int r;
        final int version;

        public QueryImpl(int l, int r, int version) {
            this.l = l;
            this.r = r;
            this.version = version;
        }

        @Override
        public void setAnswer(ModifiableMOAlgorithm.Interval<Integer> interval) {
            answer = ((IntervalImpl) interval).noneZeroNumber;
        }

        @Override
        public int getLeft() {
            return l;
        }

        @Override
        public int getRight() {
            return r;
        }

        @Override
        public int getVersion() {
            return version;
        }
    }

    public static class ModifiableMOAlgorithm<T> {
        public static interface VersionQueries<T> {
            void setAnswer(Interval<T> interval);

            int getLeft();

            int getRight();

            int getVersion();
        }

        public static interface Modification<T> {
            public int index();

            public T invoke();

            public T revoke();
        }

        public static interface Interval<T> {
            void add(T add);

            void remove(T remove);

            void clear();
        }

        public void setData(T[] data) {
            this.data = data;
        }

        public void setQueries(VersionQueries<T>[] queries) {
            this.queries = queries.clone();
        }

        public void setInterval(Interval<T> interval) {
            this.interval = interval;
        }

        public void solve() {
            int n = data.length;
            int q = queries.length;
            int m = modifications.length;

            if (n == 0 || q == 0) {
                return;
            }
            if (m == 0) {
                m = 1;
            }

            int k = Math.max(1, Mathematics.intRound(Math.pow((double) 1 / q * n * n * m, 1.0 / 3)));

            Arrays.sort(queries, new Comparator<VersionQueries<T>>() {
                @Override
                public int compare(VersionQueries<T> o1, VersionQueries<T> o2) {
                    int c = o1.getLeft() / k - o2.getLeft() / k;
                    if (c == 0) {
                        c = o1.getVersion() / k - o2.getVersion() / k;
                    }
                    if (c == 0) {
                        c = o1.getRight() - o2.getRight();
                    }
                    return c;
                }
            });

            interval.clear();
            int left = queries[0].getLeft();
            int right = left - 1;
            int version = 0;
            for (int i = 0; i < q; i++) {
                VersionQueries<T> query = queries[i];
                int l = query.getLeft();
                int r = query.getRight();
                int v = query.getVersion();

                while (left > l) {
                    interval.add(data[--left]);
                }

                while (right < r) {
                    interval.add(data[++right]);
                }

                while (left < l) {
                    interval.remove(data[left++]);
                }

                while (right > r) {
                    interval.remove(data[right--]);
                }

                while (version < v) {
                    Modification<T> modification = modifications[version++];
                    int index = modification.index();
                    data[index] = modification.invoke();
                    if (index >= l && index <= r) {
                        interval.remove(modification.revoke());
                        interval.add(data[index]);
                    }
                }

                while (version > v) {
                    Modification<T> modification = modifications[--version];
                    int index = modification.index();
                    data[index] = modification.revoke();
                    if (index >= l && index <= r) {
                        interval.remove(modification.invoke());
                        interval.add(data[index]);
                    }
                }

                query.setAnswer(interval);
            }

            while (version > 0) {
                Modification<T> modification = modifications[--version];
                int index = modification.index();
                data[index] = modification.revoke();
            }

            return;
        }

        public void setModifications(Modification<T>[] modifications) {
            this.modifications = modifications;
        }

        T[] data;
        VersionQueries<T>[] queries;
        Interval<T> interval;
        Modification<T>[] modifications;
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
