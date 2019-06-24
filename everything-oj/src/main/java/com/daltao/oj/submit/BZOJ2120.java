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

public class BZOJ2120 {
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

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();
            int[] colors = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                colors[i] = io.readInt();
            }

            Helper helper = new Helper();
            List<Query> queryList = new ArrayList(m);
            List<Modify> modifyList = new ArrayList(m);
            for (int i = 0; i < m; i++) {
                char c = io.readChar();
                if (c == 'Q') {
                    Query query = new Query();
                    query.l = io.readInt();
                    query.r = io.readInt();
                    query.v = modifyList.size();
                    queryList.add(query);
                } else {
                    Modify modify = new Modify();
                    modify.index = io.readInt();
                    modify.change = io.readInt();
                    modify.origin = colors[modify.index];
                    colors[modify.index] = modify.change;
                    modifyList.add(modify);
                }
            }

            Query[] queries = queryList.toArray(new Query[0]);
            final int blockSize = (int) Math.ceil(Math.pow((double) n * n * m / Math.max(queries.length, 1), 1.0 / 3));
            Arrays.sort(queries, new Comparator<Query>() {
                @Override
                public int compare(Query a, Query b) {
                    int d = a.l / blockSize - b.l / blockSize;
                    if (d == 0) {
                        d = a.v / blockSize - b.v / blockSize;
                    }
                    if (d == 0) {
                        d = a.r - b.r;
                    }
                    return d;
                }
            });

            int l = 1;
            int r = 0;
            int v = modifyList.size();
            for (Query q : queries) {
                while (v < q.v) {
                    Modify modify = modifyList.get(v);
                    if (include(modify.index, l, r)) {
                        helper.removeColor(colors[modify.index]);
                    }
                    colors[modify.index] = modify.change;
                    if (include(modify.index, l, r)) {
                        helper.addColor(colors[modify.index]);
                    }
                    v++;
                }
                while (v > q.v) {
                    v--;
                    Modify modify = modifyList.get(v);
                    if (include(modify.index, l, r)) {
                        helper.removeColor(colors[modify.index]);
                    }
                    colors[modify.index] = modify.origin;
                    if (include(modify.index, l, r)) {
                        helper.addColor(colors[modify.index]);
                    }
                }
                while (r < q.r) {
                    r++;
                    helper.addColor(colors[r]);
                }
                while (l > q.l) {
                    l--;
                    helper.addColor(colors[l]);
                }
                while (r > q.r) {
                    helper.removeColor(colors[r]);
                    r--;
                }
                while (l < q.l) {
                    helper.removeColor(colors[l]);
                    l++;
                }
                q.ans = helper.cnt;
            }

            for (Query q : queryList) {
                io.cache.append(q.ans).append('\n');
            }
        }

        public boolean include(int x, int l, int r) {
            return l <= x && x <= r;
        }
    }

    static class Query {
        int l;
        int r;
        int v;
        int ans;
    }

    static class Modify {
        int index;
        int change;
        int origin;
    }

    static class Helper {
        int[] colors = new int[1000000 + 1];
        int cnt = 0;

        public void addColor(int c) {
            colors[c]++;
            if (colors[c] == 1) {
                cnt++;
            }
        }

        public void removeColor(int c) {
            colors[c]--;
            if (colors[c] == 0) {
                cnt--;
            }
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

        public void flush() {
            try {
                os.write(cache.toString().getBytes(charset));
                os.flush();
                cache.setLength(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
