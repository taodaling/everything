package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class LOJ2169 {
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

        int n;
        int m;
        Segment segment;
        Event[] events;

        public void solve() {
            n = io.readInt();
            m = io.readInt();
            Country[] countries = new Country[n + 1];
            for (int i = 1; i <= n; i++) {
                countries[i] = new Country();
            }

            int[] belong = new int[m + 1];
            for (int i = 1; i <= m; i++) {
                belong[i] = io.readInt();
                countries[belong[i]].cnt++;
            }

            for (int i = 1; i <= n; i++) {
                countries[i].range = new int[countries[i].cnt];
            }
            for (int i = 1; i <= m; i++) {
                int c = belong[i];
                countries[c].range[--countries[c].cnt] = i;
            }

            for (int i = 1; i <= n; i++) {
                countries[i].req = io.readLong();
            }

            segment = new Segment(1, m);

            int k = io.readInt();
            events = new Event[k * 2];
            for (int i = 0; i < k; i++) {
                int l = io.readInt();
                int r = io.readInt();
                int a = io.readInt();

                Event e1 = new Event();
                Event e2 = new Event();
                if (l <= r) {
                    e1.l = l;
                    e1.r = r;
                    e1.a = a;
                } else {
                    e1.l = l;
                    e1.r = m;
                    e1.a = a;

                    e2.l = 1;
                    e2.r = r;
                    e2.a = a;
                }

                events[i * 2] = e1;
                events[i * 2 + 1] = e2;
            }

            dac(countries.clone(), 1, n, 0, 2 * k - 1);
            for (int i = 1; i <= n; i++) {
                if (countries[i].ans == -1) {
                    io.cache.append("NIE");
                } else {
                    io.cache.append(countries[i].ans / 2 + 1);
                }
                io.cache.append('\n');
            }
        }

        public void dac(Country[] cts, int ctsBegin, int ctsEnd, int l, int r) {
            if(ctsBegin > ctsEnd){
                return;
            }

            int mid = (l + r) >> 1;
            for (int i = l; i <= mid; i++) {
                Event e = events[i];
                segment.update(e.l, e.r, 1, m, e.a);
            }


            int right = ctsEnd;
            int left = ctsBegin;
            for (; left <= right; left++) {
                Country ct = cts[left];
                long sum = 0;
                for (int j : ct.range) {
                    sum += segment.query(j, j, 1, m);
                }
                if (sum >= ct.req) {
                    Country tmp = cts[right];
                    cts[right] = cts[left];
                    cts[left] = tmp;
                    left--;
                    right--;
                }
            }

            if(l == r) {
                for (int i = ctsBegin; i <= right; i++) {
                    cts[i].ans = -1;
                }
                for (int i = right + 1; i <= ctsEnd; i++) {
                    cts[i].ans = l;
                }
            }


            if (l < r) {
                dac(cts, ctsBegin, right, mid + 1, r);
            }

            //revoke
            for (int i = mid; i >= l; i--) {
                Event e = events[i];
                segment.update(e.l, e.r, 1, m, -e.a);
            }

            if (l < r) {
                dac(cts, right + 1, ctsEnd, l, mid);
            }
        }
    }

    public static class Event {
        int l;
        int r;
        int a;
    }

    public static class Country {
        int[] range;
        int cnt;
        int ans;
        long req;
    }

    public static class Segment implements Cloneable {
        private Segment left;
        private Segment right;
        private long cnt;
        private long dirty;

        public void setDirty(long d) {
            dirty += d;
            cnt += d;
        }

        public void pushUp() {
        }

        public void pushDown() {
            if (dirty != 0) {
                left.setDirty(dirty);
                right.setDirty(dirty);
                dirty = 0;
            }
        }

        public Segment(int l, int r) {
            if (l < r) {
                int m = (l + r) >> 1;
                left = new Segment(l, m);
                right = new Segment(m + 1, r);
                pushUp();
            } else {
            }
        }

        private boolean covered(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        private boolean noIntersection(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public void update(int ll, int rr, int l, int r, int d) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                setDirty(d);
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.update(ll, rr, l, m, d);
            right.update(ll, rr, m + 1, r, d);
            pushUp();
        }

        public long query(int ll, int rr, int l, int r) {
            if (noIntersection(ll, rr, l, r)) {
                return 0;
            }
            if (covered(ll, rr, l, r)) {
                return cnt;
            }
            pushDown();
            int m = (l + r) >> 1;
            return left.query(ll, rr, l, m) +
                    right.query(ll, rr, m + 1, r);
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
