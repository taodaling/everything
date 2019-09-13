package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LUOGU3332 {
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

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();

            List<Query> queryList = new ArrayList<>(n + 2 * m);
            int[] board = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                Query q = new Query();
                board[i] = io.readInt();
                q.l = board[i];
                q.r = i;
                q.k = 1;
                q.type = 0;
                queryList.add(q);
            }

            for (int i = 1; i <= m; i++) {
                if (io.readChar() == 'Q') {
                    Query q = new Query();
                    q.type = 1;
                    q.l = io.readInt();
                    q.r = io.readInt();
                    q.k = io.readInt();
                    queryList.add(q);
                } else {
                    int idx = io.readInt();
                    int rep = io.readInt();

                    Query q = new Query();
                    q.type = 0;
                    q.l = board[idx];
                    q.r = idx;
                    q.k = -1;
                    queryList.add(q);

                    board[idx] = rep;
                    q = new Query();
                    q.type = 0;
                    q.l = board[idx];
                    q.r = idx;
                    q.k = 1;
                    queryList.add(q);
                }
            }

            bit = new BIT(n);
            dac(queryList.toArray(new Query[0]), 0, queryList.size() - 1,
                    0, (int) 1e9, queryList.toArray(new Query[0]));

            for (Query q : queryList) {
                if (q.type == 1) {
                    io.cache.append(q.ans).append('\n');
                }
            }
        }

        BIT bit;

        public void dac(Query[] qs, int ql, int qr, int vl, int vr, Query[] buf) {
            if (ql > qr) {
                return;
            }

            if (vl == vr) {
                for (int i = ql; i <= qr; i++) {
                    if (qs[i].type == 1) {
                        qs[i].ans = vl;
                    }
                }
                return;
            }

            int m = (vl + vr) / 2;
            for (int i = ql; i <= qr; i++) {
                if (qs[i].type == 0) {
                    if (qs[i].l <= m) {
                        bit.update(qs[i].r, qs[i].k);
                        qs[i].tag = 0;
                    } else {
                        qs[i].tag = 1;
                    }
                } else {
                    int cnt = bit.query(qs[i].r) - bit.query(qs[i].l - 1);
                    if (cnt >= qs[i].k) {
                        qs[i].tag = 0;
                    } else {
                        qs[i].k -= cnt;
                        qs[i].tag = 1;
                    }
                }
            }

            int wpos = ql;
            for (int i = ql; i <= qr; i++) {
                if (qs[i].tag == 0) {
                    buf[wpos++] = qs[i];
                }
            }
            int sep = wpos;
            for (int i = ql; i <= qr; i++) {
                if (qs[i].tag == 1) {
                    buf[wpos++] = qs[i];
                }
            }
            System.arraycopy(buf, ql, qs, ql, qr - ql + 1);

            for (int i = ql; i < sep; i++) {
                if (qs[i].type == 0) {
                    bit.update(qs[i].r, -qs[i].k);
                }
            }
            dac(qs, sep, qr, m + 1, vr, buf);
            dac(qs, ql, sep - 1, vl, m, buf);
        }
    }

    public static class Query {
        int type; //0 for add, 1 for query
        int l; //v
        int r; //i
        int k; //c
        int ans;
        int tag;

        @Override
        public String toString() {
            if(type == 0)
            {
                return "C " + r + "," + l + "," + k;
            }
            return "Q (" + l + "," + r + "," + k + ")";
         }
    }


    /**
     * Created by dalt on 2018/5/20.
     */
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

        /**
         * 将A全部清0
         */
        public void clear() {
            Arrays.fill(data, 0);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                builder.append(query(i) - query(i - 1)).append(' ');
            }
            return builder.toString();
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
