package com.daltao.oj.old.submit.poj;

import java.io.*;
import java.nio.charset.Charset;

public class POJ2104V2 {
    public static void main(String[] args) throws FileNotFoundException {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        IOUtil io;
        if (local) {
            io = new IOUtil(new FileInputStream("E:\\DATABASE\\TESTCASE\\poj\\POJ2104.in"), new FileOutputStream("E:\\DATABASE\\TESTCASE\\poj\\POJ2104.out"));
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
            Segment[] segments = new Segment[n + 1];
            segments[0] = Segment.build();
            int MIN = (int) -1e9;
            int MAX = (int) 1e9;
            for (int i = 1; i <= n; i++) {
                int v = io.readInt();
                segments[i] = Segment.updatePersistently(v, v, MIN, MAX, 1, segments[i - 1]);
            }
            for (int i = 1; i <= m; i++) {
                int l = io.readInt();
                int r = io.readInt();
                int k = io.readInt();

                int val = Segment.query(MIN, MAX, k, segments[l - 1], segments[r]);
                io.write(val );
                io.write('\n');
            }
        }
    }

    public static class Segment implements Cloneable {
        Segment left;
        Segment right;
        int cnt;


        public static Segment build() {
            Segment segment = new Segment();
            segment.left = segment.right = segment;
            segment.cnt = 0;
            return segment;
        }

        public static boolean checkOutOfRange(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public static boolean checkCoverage(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        public static Segment updatePersistently(int ll, int rr, int l, int r, int x, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return segment;
            }
            segment = segment.clone();
            if (checkCoverage(ll, rr, l, r)) {
                segment.cnt += x;
                return segment;
            }

            int m = (l + r) >> 1;

            segment.pushDown();
            segment.left = updatePersistently(ll, rr, l, m, x, segment.left);
            segment.right = updatePersistently(ll, rr, m + 1, r, x, segment.right);
            segment.pushUp();
            return segment;
        }

        public static int query(int l, int r, int k, Segment s1, Segment s2) {
            while (l != r) {
                int m = (l + r) >> 1;
                s1.pushDown();
                s2.pushDown();
                if (s2.left.cnt - s1.left.cnt >= k) {
                    s1 = s1.left;
                    s2 = s2.left;
                    r = m;
                } else {
                    k -= s2.left.cnt - s1.left.cnt;
                    s1 = s1.right;
                    s2 = s2.right;
                    l = m + 1;
                }
            }
            return l;
        }

        public void pushDown() {
        }

        public void pushUp() {
            cnt = left.cnt + right.cnt;
        }

        @Override
        public Segment clone() {
            try {
                return (Segment) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
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
            while (r_next <= 32) {
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
}
