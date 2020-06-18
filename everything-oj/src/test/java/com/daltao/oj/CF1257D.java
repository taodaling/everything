package com.daltao.oj;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.function.BiFunction;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;

public class CF1257D {

    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(RightSolution.class)))
                .setTestTime(10000)
                .build().call());
    }


    /**
     * Built using CHelper plug-in Actual solution is at the top
     */
    public static class Main {
        public static void main(String[] args) throws Exception {
            Thread thread = new Thread(null, new TaskAdapter(), "", 1 << 27);
            thread.start();
            thread.join();
        }

        static class TaskAdapter implements Runnable {
            @Override
            public void run() {
                InputStream inputStream = System.in;
                OutputStream outputStream = System.out;
                FastInput in = new FastInput(inputStream);
                FastOutput out = new FastOutput(outputStream);
                TaskD solver = new TaskD();
                int testCount = Integer.parseInt(in.next());
                for (int i = 1; i <= testCount; i++)
                    solver.solve(i, in, out);
                out.close();
            }
        }
        static class TaskD {
            public void solve(int testNumber, FastInput in, FastOutput out) {
                int n = in.readInt();
                Integer[] monster = new Integer[n];

                for (int i = 0; i < n; i++) {
                    monster[i] = in.readInt();
                }
                SparseTable<Integer> st = new SparseTable<>(monster, n, (a, b) -> a > b ? a : b);
                int m = in.readInt();
                int[] s = new int[n + 1];
                for (int i = 0; i < m; i++) {
                    int p = in.readInt();
                    int live = in.readInt();
                    s[live] = Math.max(s[live], p);
                }
                for (int i = n - 1; i >= 1; i--) {
                    s[i] = Math.max(s[i], s[i + 1]);
                }

                if (st.query(0, n - 1) > s[1]) {
                    out.println(-1);
                    return;
                }

                int day = 0;
                int pos = 0;

                while (pos < n) {
                    int l = pos;
                    int r = n - 1;
                    while (l < r) {
                        int mid = (l + r + 1) / 2;
                        if (st.query(0, mid) > s[mid - pos + 1]) {
                            r = mid - 1;
                        } else {
                            l = mid;
                        }
                    }

                    day++;
                    pos = l + 1;
                }

                out.println(day);
            }

        }
        static class SparseTable<T> {
            private Object[][] st;
            private BiFunction<T, T, T> merger;
            private DigitUtils.CachedLog2 log2;

            public SparseTable(Object[] data, int length, BiFunction<T, T, T> merger) {
                log2 = new DigitUtils.CachedLog2(length);
                int m = log2.floorLog(length);

                st = new Object[m + 1][length];
                this.merger = merger;
                for (int i = 0; i < length; i++) {
                    st[0][i] = data[i];
                }
                for (int i = 0; i < m; i++) {
                    int interval = 1 << i;
                    for (int j = 0; j < length; j++) {
                        if (j + interval < length) {
                            st[i + 1][j] = merge((T) st[i][j], (T) st[i][j + interval]);
                        } else {
                            st[i + 1][j] = st[i][j];
                        }
                    }
                }
            }

            private T merge(T a, T b) {
                return merger.apply(a, b);
            }

            public T query(int left, int right) {
                int queryLen = right - left + 1;
                int bit = log2.floorLog(queryLen);
                // x + 2^bit == right + 1
                // So x should be right + 1 - 2^bit - left=queryLen - 2^bit
                return merge((T) st[bit][left], (T) st[bit][right + 1 - (1 << bit)]);
            }

            public String toString() {
                return Arrays.toString(st[0]);
            }

        }
        static class DigitUtils {
            private DigitUtils() {}

            public static class Log2 {
                public int floorLog(int x) {
                    return 31 - Integer.numberOfLeadingZeros(x);
                }

            }

            public static class CachedLog2 {
                private int[] cache;
                private DigitUtils.Log2 log2;

                public CachedLog2(int n) {
                    cache = new int[n + 1];
                    int b = 0;
                    for (int i = 0; i <= n; i++) {
                        while ((1 << (b + 1)) <= i) {
                            b++;
                        }
                        cache[i] = b;
                    }
                }

                public int floorLog(int x) {
                    if (x >= cache.length) {
                        return log2.floorLog(x);
                    }
                    return cache[x];
                }

            }

        }
        static class FastOutput implements AutoCloseable, Closeable {
            private StringBuilder cache = new StringBuilder(10 << 20);
            private final Writer os;

            public FastOutput(Writer os) {
                this.os = os;
            }

            public FastOutput(OutputStream os) {
                this(new OutputStreamWriter(os));
            }

            public FastOutput println(int c) {
                cache.append(c).append('\n');
                return this;
            }

            public FastOutput flush() {
                try {
                    os.append(cache);
                    os.flush();
                    cache.setLength(0);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return this;
            }

            public void close() {
                flush();
                try {
                    os.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public String toString() {
                return cache.toString();
            }

        }
        static class FastInput {
            private final InputStream is;
            private StringBuilder defaultStringBuf = new StringBuilder(1 << 13);
            private byte[] buf = new byte[1 << 13];
            private int bufLen;
            private int bufOffset;
            private int next;

            public FastInput(InputStream is) {
                this.is = is;
            }

            private int read() {
                while (bufLen == bufOffset) {
                    bufOffset = 0;
                    try {
                        bufLen = is.read(buf);
                    } catch (IOException e) {
                        bufLen = -1;
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

            public String next() {
                return readString();
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

        }
    }



    /**
     * Built using CHelper plug-in
     * Actual solution is at the top
     *
     * @author mikit
     */
    public static class RightSolution {
        public static void main(String[] args) {
            InputStream inputStream = System.in;
            OutputStream outputStream = System.out;
            LightScanner in = new LightScanner(inputStream);
            LightWriter out = new LightWriter(outputStream);
            DYetAnotherMonsterKillingProblem solver = new DYetAnotherMonsterKillingProblem();
            solver.solve(1, in, out);
            out.close();
        }

        static class DYetAnotherMonsterKillingProblem {
            public void solve(int testNumber, LightScanner in, LightWriter out) {
                // out.setBoolLabel(LightWriter.BoolLabel.YES_NO_FIRST_UP);
                int testCases = in.ints();
                loop:
                for (int testCase = 0; testCase < testCases; testCase++) {
                    int monsters = in.ints();
                    long[] mp = in.longs(monsters);
                    IntSegmentTree hp = new IntSegmentTree(mp, Math::max, 0, Math::max);
                    int heroes = in.ints();
                    IntSegmentTree st;
                    {
                        long[] neg = new long[monsters + 1];
                        Arrays.fill(neg, Integer.MIN_VALUE);
                        st = new IntSegmentTree(neg, Math::max, 0, Math::max);
                        for (int i = 0; i < heroes; i++) {
                            long p = in.longs();
                            int s = in.ints();
                            st.update(s, p);
                        }
                    }
                    int ans = 0;
                    for (int i = 0; i < monsters; ) {
                        int ok = i, ng = monsters + 1;
                        while (ng - ok > 1) {
                            int mid = (ok + ng) / 2;
                            if (st.query(mid - i, monsters + 1) < hp.query(i, mid)) {
                                ng = mid;
                            } else ok = mid;
                        }
                        //System.out.println("i=" + i + " to " + ok);
                        if (ok == i) {
                            out.ans(-1).ln();
                            continue loop;
                        }
                        ans++;
                        i = ok;
                    }
                    out.ans(ans).ln();
                }
            }

        }

        static class IntSegmentTree {
            private final int n;
            private final int m;
            private final long[] tree;
            private final LongBinaryOperator op;
            private final LongBinaryOperator up;
            private final long zero;

            public IntSegmentTree(long[] array, LongBinaryOperator op, long zero, LongBinaryOperator up) {
                this.n = array.length;
                int msb = BitMath.extractMsb(n);
                this.m = n == msb ? msb : (msb << 1);
                this.tree = new long[m * 2 - 1];
                this.op = op;
                this.up = up;
                this.zero = zero;
                Arrays.fill(tree, zero);
                System.arraycopy(array, 0, this.tree, m - 1, array.length);
                for (int i = m - 2; i >= 0; i--) {
                    tree[i] = op.applyAsLong(tree[2 * i + 1], tree[2 * i + 2]);
                }
            }

            public void update(int i, long v) {
                i += m - 1;
                tree[i] = up.applyAsLong(tree[i], v);
                while (i > 0) {
                    i = (i - 1) / 2;
                    tree[i] = op.applyAsLong(tree[2 * i + 1], tree[2 * i + 2]);
                }
            }

            public long query(int l, int r) {
                long left = zero, right = zero;
                l += m - 1;
                r += m - 1;
                while (l < r) {
                    if ((l & 1) == 0) {
                        left = op.applyAsLong(left, tree[l]);
                    }
                    if ((r & 1) == 0) {
                        right = op.applyAsLong(tree[r - 1], right);
                    }
                    l = l / 2;
                    r = (r - 1) / 2;
                }
                return op.applyAsLong(left, right);
            }

        }

        static class LightWriter implements AutoCloseable {
            private final Writer out;
            private boolean autoflush = false;
            private boolean breaked = true;

            public LightWriter(Writer out) {
                this.out = out;
            }

            public LightWriter(OutputStream out) {
                this(new BufferedWriter(new OutputStreamWriter(out, Charset.defaultCharset())));
            }

            public LightWriter print(char c) {
                try {
                    out.write(c);
                    breaked = false;
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
                return this;
            }

            public LightWriter print(String s) {
                try {
                    out.write(s, 0, s.length());
                    breaked = false;
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
                return this;
            }

            public LightWriter ans(String s) {
                if (!breaked) {
                    print(' ');
                }
                return print(s);
            }

            public LightWriter ans(int i) {
                return ans(Integer.toString(i));
            }

            public LightWriter ln() {
                print(System.lineSeparator());
                breaked = true;
                if (autoflush) {
                    try {
                        out.flush();
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }
                return this;
            }

            public void close() {
                try {
                    out.close();
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }

        }

        static class LightScanner {
            private BufferedReader reader = null;
            private StringTokenizer tokenizer = null;

            public LightScanner(InputStream in) {
                reader = new BufferedReader(new InputStreamReader(in));
            }

            public String string() {
                if (tokenizer == null || !tokenizer.hasMoreTokens()) {
                    try {
                        tokenizer = new StringTokenizer(reader.readLine());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
                return tokenizer.nextToken();
            }

            public int ints() {
                return Integer.parseInt(string());
            }

            public long longs() {
                return Long.parseLong(string());
            }

            public long[] longs(int length) {
                return IntStream.range(0, length).mapToLong(x -> longs()).toArray();
            }

        }

        static final class BitMath {
            private BitMath() {
            }

            public static int extractMsb(int v) {
                v = (v & 0xFFFF0000) > 0 ? v & 0xFFFF0000 : v;
                v = (v & 0xFF00FF00) > 0 ? v & 0xFF00FF00 : v;
                v = (v & 0xF0F0F0F0) > 0 ? v & 0xF0F0F0F0 : v;
                v = (v & 0xCCCCCCCC) > 0 ? v & 0xCCCCCCCC : v;
                v = (v & 0xAAAAAAAA) > 0 ? v & 0xAAAAAAAA : v;
                return v;
            }

        }
    }


    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput in = new QueueInput();
            in.add(1);
            int n = nextInt(1, 5);
            in.add(n);
            StringBuilder monster = new StringBuilder();
            for (int i = 0; i < n; i++) {
                monster.append(nextInt(1, 10)).append(' ');
            }
            in.add(monster.toString());
            int m = nextInt(1, 5);
            in.add(m);
            StringBuilder hero = new StringBuilder();
            for (int i = 0; i < m; i++) {
                hero.append(nextInt(1, 5)).append(' ').append(nextInt(1, n)).append('\n');
            }
            in.add(hero.toString());
            return in.end();
        }
    }
}
