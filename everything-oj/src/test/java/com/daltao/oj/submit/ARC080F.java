package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.TreeSet;

public class ARC080F {

    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Task.class)))
                .setTestTime(1000)
                .build()
                .call());
    }

    /**
     * Built using CHelper plug-in Actual solution is at the top
     *
     * @author daltao
     */
    public static class Task {
        public static void main(String[] args) throws Exception {
            Thread thread = new Thread(null, new TaskAdapter(), "daltao", 1 << 27);
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
                solver.solve(1, in, out);
                out.close();
            }
        }

        static class TaskD {
            NumberTheory.EulerSieve es = new NumberTheory.EulerSieve(10000000);
            int[][][] dp;
            int[] xs;
            int n;

            public void solve(int testNumber, FastInput in, FastOutput out) {
                n = in.readInt();
                xs = new int[n];
                for (int i = 0; i < n; i++) {
                    xs[i] = in.readInt();
                }

                dp = new int[2][n][n];
                SequenceUtils.deepFill(dp, -1);
                int ans = dp(0, n - 1);
                out.println(ans);
            }

            public int feeOnFlip(int len) {
                if (len == 0) {
                    return 0;
                }
                if (len == 2) {
                    return 2;
                }
                if (es.isPrime(len)) {
                    return 1;
                }
                return len % 2 + 2;
            }

            public int dp(int l, int r) {
                if (dp[0][l][r] == -1) {
                    if (l == r) {
                        return dp[0][l][r] = feeOnFlip(1);
                    }
                    dp[0][l][r] = feeOnFlip(xs[r] - xs[l] + 1) + dpFlipped(l, r);
                    for (int i = l; i < r; i++) {
                        dp[0][l][r] = Math.min(dp[0][l][r], dp(l, i) + dp(i + 1, r));
                    }
                }
                return dp[0][l][r];
            }

            public int dpFlipped(int l, int r) {
                if (dp[1][l][r] == -1) {
                    if (l == r) {
                        return dp[1][l][r] = 0;
                    }
                    if (l + 1 == r) {
                        return dp[1][l][r] = feeOnFlip(xs[r] - xs[l] - 1);
                    }
                    // flip or sep
                    dp[1][l][r] = feeOnFlip(xs[r] - xs[l] - 1) + dp(l + 1, r - 1);
                    for (int i = l + 1; i < r; i++) {
                        dp[1][l][r] = Math.min(dp[1][l][r], dpFlipped(l, i) + dpFlipped(i, r));
                    }
                }
                return dp[1][l][r];
            }

        }

        static class NumberTheory {
            public static class EulerSieve {
                private int[] primes;
                private boolean[] isComp;
                private int primeLength;

                public boolean isPrime(int x) {
                    if (x == 1) {
                        return false;
                    }
                    return !isComp[x];
                }

                public EulerSieve(int limit) {
                    isComp = new boolean[limit + 1];
                    primes = new int[limit + 1];
                    primeLength = 0;
                    for (int i = 2; i <= limit; i++) {
                        if (!isComp[i]) {
                            primes[primeLength++] = i;
                        }
                        for (int j = 0, until = limit / i; j < primeLength && primes[j] <= until; j++) {
                            int pi = primes[j] * i;
                            isComp[pi] = true;
                            if (i % primes[j] == 0) {
                                break;
                            }
                        }
                    }
                }

            }

        }

        static class SequenceUtils {
            public static void deepFill(Object array, int val) {
                if (!array.getClass().isArray()) {
                    throw new IllegalArgumentException();
                }
                if (array instanceof int[]) {
                    int[] intArray = (int[]) array;
                    Arrays.fill(intArray, val);
                } else {
                    Object[] objArray = (Object[]) array;
                    for (Object obj : objArray) {
                        deepFill(obj, val);
                    }
                }
            }

        }

        static class FastInput {
            private final InputStream is;
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

        }
    }


    public static class Main {
        static InputStream is;
        static PrintWriter out;
        static String INPUT = "";

        static void solve() {
            int n = ni();
            int[] a = na(n);
            int[] b = new int[2 * n];
            int p = 0;
            for (int i = 0; i < n; i++) {
                b[p++] = a[i];
                b[p++] = a[i] + 1;
            }
            int q = 0;
            for (int i = 0; i < 2 * n; i++) {
                if (i + 1 < 2 * n && b[i] == b[i + 1]) {
                    i++;
                    continue;
                }
                b[q++] = b[i];
            }
            b = Arrays.copyOf(b, q);

            int pe = 0, po = 0;
            int[] be = new int[q];
            int[] bo = new int[q];
            for (int v : b) {
                if (v % 2 == 0) {
                    be[pe++] = v;
                } else {
                    bo[po++] = v;
                }
            }
            be = Arrays.copyOf(be, pe);
            bo = Arrays.copyOf(bo, po);

            long[] isp = isp(11000000);
            boolean[][] g = new boolean[pe][po];
            for (int i = 0; i < pe; i++) {
                for (int j = 0; j < po; j++) {
                    int s = Math.abs(be[i] - bo[j]);
                    if (isp[s >>> 6] << ~s < 0) {
                        g[i][j] = true;
                    }
                }
            }
            int F = doBipartiteMatchingHK(g);
            out.println(F + (pe - F) / 2 * 2 + (po - F) / 2 * 2 + ((pe - F) % 2 == 1 ? 3 : 0));
        }

        public static int doBipartiteMatchingHK(boolean[][] g) {
            int n = g.length;
            if (n == 0) return 0;
            int m = g[0].length;
            int[] from = new int[m];
            int[] to = new int[n];
            Arrays.fill(to, -1);
            Arrays.fill(from, n);

            int[] d = new int[n + 1];
            int mat = 0;
            while (true) {
                Arrays.fill(d, -1);
                int[] q = new int[n];
                int r = 0;
                for (int i = 0; i < n; i++) {
                    if (to[i] == -1) {
                        d[i] = 0;
                        q[r++] = i;
                    }
                }

                for (int p = 0; p < r; p++) {
                    int cur = q[p];
                    for (int adj = 0; adj < m; adj++) {
                        if (g[cur][adj]) {
                            int nex = from[adj];
                            if (d[nex] == -1) {
                                if (nex != n) q[r++] = nex;
                                d[nex] = d[cur] + 1;
                            }
                        }
                    }
                }
                if (d[n] == -1) break;

                for (int i = 0; i < n; i++) {
                    if (to[i] == -1) {
                        if (dfsHK(d, g, n, m, to, from, i)) mat++;
                    }
                }
            }

            return mat;
        }

        static boolean dfsHK(int[] d, boolean[][] g, int n, int m, int[] to, int[] from, int cur) {
            if (cur == n) return true;
            for (int adj = 0; adj < m; adj++) {
                if (g[cur][adj]) {
                    int nex = from[adj];
                    if (d[nex] == d[cur] + 1 && dfsHK(d, g, n, m, to, from, nex)) {
                        to[cur] = adj;
                        from[adj] = cur;
                        return true;
                    }
                }
            }
            d[cur] = -1;
            return false;
        }


        public static long[] isp(int n) {
            int[] tprimes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};
            if (n <= 64) {
                long ptn = 0;
                for (int p : tprimes) if (p <= n) ptn |= 1L << p;
                return new long[]{ptn};
            }

            long[] isnp = new long[(n + 1) / 64 + 1];
            int sup = (n + 1) / 64 + 1;

            isnp[0] |= 1 << 1;
            for (int tp : tprimes) {
                long[] ptn = new long[tp];
                for (int i = 0; i < tp << 6; i += tp) ptn[i >>> 6] |= 1L << i;
                for (int j = 0; j < sup; j += tp) {
                    for (int i = 0; i < tp && i + j < sup; i++) {
                        isnp[j + i] |= ptn[i];
                    }
                }
            }

            final int[] magic = {0, 1, 2, 53, 3, 7, 54, 27, 4, 38, 41, 8, 34, 55, 48, 28, 62, 5, 39, 46, 44, 42, 22, 9, 24, 35, 59, 56, 49, 18, 29, 11, 63, 52, 6, 26, 37, 40, 33, 47, 61, 45, 43, 21, 23, 58, 17, 10, 51, 25, 36, 32,
                    60, 20, 57, 16, 50, 31, 19, 15, 30, 14, 13, 12};
            out:
            for (int i = 0; i < sup; i++) {
                for (long j = ~isnp[i]; j != 0; j &= j - 1) {
                    int p = i << 6 | magic[(int) ((j & -j) * 0x022fdd63cc95386dL >>> 58)];
                    if ((long) p * p > n) break out;
                    for (int q = p * p; q <= n; q += p) isnp[q >> 6] |= 1L << q;
                }
            }

            for (int i = 0; i < isnp.length; i++) isnp[i] = ~isnp[i];
            for (int tp : tprimes) isnp[0] |= 1L << tp;
            isnp[isnp.length - 1] &= (1L << n + 1) - 1;

            return isnp;
        }


        public static void main(String[] args) throws Exception {
            long S = System.currentTimeMillis();
            is = INPUT.isEmpty() ? System.in : new ByteArrayInputStream(INPUT.getBytes());
            out = new PrintWriter(System.out);

            solve();
            out.flush();
            long G = System.currentTimeMillis();
            tr(G - S + "ms");
        }

        private static boolean eof() {
            if (lenbuf == -1) return true;
            int lptr = ptrbuf;
            while (lptr < lenbuf) if (!isSpaceChar(inbuf[lptr++])) return false;

            try {
                is.mark(1000);
                while (true) {
                    int b = is.read();
                    if (b == -1) {
                        is.reset();
                        return true;
                    } else if (!isSpaceChar(b)) {
                        is.reset();
                        return false;
                    }
                }
            } catch (IOException e) {
                return true;
            }
        }

        private static byte[] inbuf = new byte[1024];
        static int lenbuf = 0, ptrbuf = 0;

        private static int readByte() {
            if (lenbuf == -1) throw new InputMismatchException();
            if (ptrbuf >= lenbuf) {
                ptrbuf = 0;
                try {
                    lenbuf = is.read(inbuf);
                } catch (IOException e) {
                    throw new InputMismatchException();
                }
                if (lenbuf <= 0) return -1;
            }
            return inbuf[ptrbuf++];
        }

        private static boolean isSpaceChar(int c) {
            return !(c >= 33 && c <= 126);
        }

        //	private static boolean isSpaceChar(int c) { return !(c >= 32 && c <= 126); }
        private static int skip() {
            int b;
            while ((b = readByte()) != -1 && isSpaceChar(b)) ;
            return b;
        }

        private static double nd() {
            return Double.parseDouble(ns());
        }

        private static char nc() {
            return (char) skip();
        }

        private static String ns() {
            int b = skip();
            StringBuilder sb = new StringBuilder();
            while (!(isSpaceChar(b))) {
                sb.appendCodePoint(b);
                b = readByte();
            }
            return sb.toString();
        }

        private static char[] ns(int n) {
            char[] buf = new char[n];
            int b = skip(), p = 0;
            while (p < n && !(isSpaceChar(b))) {
                buf[p++] = (char) b;
                b = readByte();
            }
            return n == p ? buf : Arrays.copyOf(buf, p);
        }

        private static char[][] nm(int n, int m) {
            char[][] map = new char[n][];
            for (int i = 0; i < n; i++) map[i] = ns(m);
            return map;
        }

        private static int[] na(int n) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = ni();
            return a;
        }

        private static int ni() {
            int num = 0, b;
            boolean minus = false;
            while ((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-')) ;
            if (b == '-') {
                minus = true;
                b = readByte();
            }

            while (true) {
                if (b >= '0' && b <= '9') {
                    num = num * 10 + (b - '0');
                } else {
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private static long nl() {
            long num = 0;
            int b;
            boolean minus = false;
            while ((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-')) ;
            if (b == '-') {
                minus = true;
                b = readByte();
            }

            while (true) {
                if (b >= '0' && b <= '9') {
                    num = num * 10 + (b - '0');
                } else {
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private static void tr(Object... o) {
            if (INPUT.length() != 0) System.out.println(Arrays.deepToString(o));
        }
    }


    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            int n = nextInt(1, 2);
            TreeSet<Integer> set = new TreeSet<>();
            for (int i = 0; i < n; i++) {
                set.add(nextInt(1, 100));
            }
            n = set.size();
            QueueInput in = new QueueInput();
            in.add(n);
            StringBuilder builder = new StringBuilder();
            for (int x : set) {
                builder.append(x).append(' ');
            }
            in.add(builder.toString());
            return in.end();
        }
    }
}
