package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LUOGU1527 {
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
            List<Element> elementList = new ArrayList<>(n * n);
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    Element e = new Element();
                    e.x = i;
                    e.y = j;
                    e.v = io.readInt();
                    elementList.add(e);
                }
            }

            List<Query> queryList = new ArrayList<>(m);
            for (int i = 0; i < m; i++) {
                Query q = new Query();
                q.x1 = io.readInt();
                q.y1 = io.readInt();
                q.x2 = io.readInt();
                q.y2 = io.readInt();
                q.k = io.readInt();
                queryList.add(q);
            }

            Element[] elements = elementList.toArray(new Element[0]);
            Arrays.sort(elements, (a, b) -> a.v - b.v);
            bit2D = new BIT2D(n, n);
            dac(queryList.toArray(new Query[0]), 0, queryList.size() - 1,
                    elements, 0, elements.length - 1, queryList.toArray(new Query[0]));
            for (Query q : queryList) {
                io.cache.append(q.ans.v).append('\n');
            }
        }

        BIT2D bit2D;

        public void dac(Query[] qs, int ql, int qr, Element[] es, int el, int er, Query[] buf) {
            if (ql > qr) {
                return;
            }
            if (el == er) {
                for (int i = ql; i <= qr; i++) {
                    qs[i].ans = es[el];
                }
                return;
            }

            int m = (el + er) / 2;
            for (int i = el; i <= m; i++) {
                bit2D.update(es[i].x, es[i].y, 1);
            }

            for (int i = ql; i <= qr; i++) {
                Query q = qs[i];
                int cnt = bit2D.rect(q.x1, q.y1, q.x2, q.y2);
                if (q.k <= cnt) {
                    q.tag = 0;
                } else {
                    q.k -= cnt;
                    q.tag = 1;
                }
            }

            for (int i = el; i <= m; i++) {
                bit2D.update(es[i].x, es[i].y, -1);
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

            dac(qs, ql, sep - 1, es, el, m, buf);
            dac(qs, sep, qr, es, m + 1, er, buf);
        }
    }

    public static class Element {
        int x;
        int y;
        int v;
    }

    public static class Query {
        int x1, y1;
        int x2, y2;
        int k;
        Element ans;
        int tag;
    }

    public static class BIT2D {
        private int[][] data;
        private int n;
        private int m;

        /**
         * 创建大小A[1...n][1..,m]
         */
        public BIT2D(int n, int m) {
            this.n = n;
            this.m = m;
            data = new int[n + 1][m + 1];
        }

        /**
         * 查询左上角为(1,1)，右下角为(x,y)的矩形和
         */
        public int query(int x, int y) {
            int sum = 0;
            for (int i = x; i > 0; i -= i & -i) {
                for (int j = y; j > 0; j -= j & -j) {
                    sum += data[i][j];
                }
            }
            return sum;
        }


        /**
         * 查询左上角为(ltx,lty)，右下角为(rbx,rby)的矩形和
         */
        public int rect(int ltx, int lty, int rbx, int rby) {
            return query(rbx, rby) - query(ltx - 1, rby) - query(rbx, lty - 1) + query(ltx - 1, lty - 1);
        }

        /**
         * 将A[x][y] 更新为A[x][y]+mod
         */
        public void update(int x, int y, int mod) {
            for (int i = x; i <= n; i += i & -i) {
                for (int j = y; j <= m; j += j & -j) {
                    data[i][j] += mod;
                }
            }
        }

        /**
         * 将A全部清0
         */
        public void clear() {
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {
                    data[i][j] = 0;
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {
                    builder.append(query(i, j) + query(i - 1, j - 1) - query(i - 1, j) - query(i, j - 1)).append(' ');
                }
                builder.append('\n');
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
