package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CF1229D {
    public static void main(String[] args) throws Exception {
        boolean local = System.getSecurityManager() == null;
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

        int[][] mat;
        int k;
        int kk;


        Map<Transform, BitSet> transform = new HashMap<>(50000);

        public void afterAddX(BitSet set, int x) {
            if (set.get(x)) {
                return;
            }
            set.set(x, true);

            for (int i = set.nextSetBit(0); i != -1; i = set.nextSetBit(i + 1)) {
                afterAddX(set, mat[i][x]);
                afterAddX(set, mat[x][i]);
            }
        }

        public BitSet generateGroup(BitSet s, Integer x) {
            Transform t = new Transform(s, x);
            BitSet ans = transform.get(t);
            if (ans != null) {
                return ans;
            }
            ans = (BitSet) s.clone();
            afterAddX(ans, x);
            transform.put(t, ans);
            return ans;
        }

        List<Integer> perms = new ArrayList<>(150);

        public void solve() {
            int n = io.readInt();
            k = io.readInt();
            genPerm(0, new boolean[k], 0, k, perms);
            int[] inverseIndex = new int[100000];
            kk = perms.size();
            for (int i = 0; i < kk; i++) {
                inverseIndex[perms.get(i)] = i;
            }
            mat = new int[kk][kk];
            for (int i = 0; i < kk; i++) {
                for (int j = 0; j < kk; j++) {
                    mat[i][j] = inverseIndex[apply(perms.get(i), perms.get(j), k)];
                }
            }
            int[] seq = new int[n];
            for (int i = 0; i < n; i++) {
                int p = 0;
                for (int j = 0; j < k; j++) {
                    p = p * 10 + io.readInt() - 1;
                }
                p = inv(p, k);
                seq[i] = inverseIndex[p];
            }

            int[] registries = new int[kk];
            Arrays.fill(registries, n);
            int[][] next = new int[n][kk];
            for (int i = n - 1; i >= 0; i--) {
                for (int j = 0; j < kk; j++) {
                    next[i][j] = registries[j];
                }
                registries[seq[i]] = i;
            }


            long ans = 0;
            BitSet empty = new BitSet(kk);

            for (int i = 0; i < n; i++) {
                BitSet record = empty;
                int last = i;
                int r;
                int l;
                for (l = i; l <= n; l = r) {
                    ans += (l - last) * record.cardinality();
                    if (l == n) {
                        break;
                    }
                    last = l;
                    record = generateGroup(record, seq[l]);
                    r = n;
                    for (int j = 0; j < kk; j++) {
                        if (next[l][j] < r && !record.get(j)) {
                            r = next[l][j];
                        }
                    }
                }
            }

            io.cache.append(ans);
        }

        int[] buf = new int[5];

        public int inv(int source, int k) {
            for (int i = 0; i < k; i++) {
                buf[i] = source % 10;
                source /= 10;
            }
            int ans = 0;
            for (int i = 0; i < k; i++) {
                ans = ans * 10 + buf[i];
            }
            return ans;
        }

        public int apply(int source, int func, int k) {
            for (int i = 0; i < k; i++) {
                buf[i] = source % 10;
                source /= 10;
            }
            int ans = 0;
            for (int i = 0; i < k; i++) {
                int p = func % 10;
                func /= 10;
                ans = ans * 10 + buf[p];
            }
            return inv(ans, k);
        }

        void genPerm(int p, boolean[] used, int i, int n, List<Integer> perms) {
            if (i == n) {
                perms.add(p);
                return;
            }
            for (int j = 0; j < n; j++) {
                if (used[j]) {
                    continue;
                }
                used[j] = true;
                genPerm(p * 10 + j, used, i + 1, n, perms);
                used[j] = false;
            }
        }
    }

    public static class Transform {
        BitSet set;
        int x;

        public Transform(BitSet set, int x) {
            this.set = set;
            this.x = x;
        }

        @Override
        public int hashCode() {
            return x * 31 + set.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            Transform other = (Transform) obj;
            return other.x == x && other.set.equals(set);
        }
    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder(1 << 13);
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 13);
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
