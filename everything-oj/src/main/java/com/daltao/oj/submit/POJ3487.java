package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class POJ3487 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("E:\\DATABASE\\TESTCASE\\POJ3487.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io);

        if (async) {
            Thread t = new Thread(null, task, "dalt", 1 << 27);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            t.join();
        } else {
            task.run();
        }

        io.flush();
    }

    public static class Task implements Runnable {
        final FastIO io;
        Male[] males = new Male[26];
        Female[] females = new Female[26];
        char[] buf = new char[26 + 2];
        final int INF = (int) 1e9;
        Deque<Male> abandon = new ArrayDeque(26);

        public Task(FastIO io) {
            this.io = io;
            for (int i = 0; i < 26; i++) {
                males[i] = new Male();
                males[i].id = i;
                males[i].version = -1;
                females[i] = new Female();
                females[i].id = i;
            }
        }

        public void init() {
        }

        @Override
        public void run() {
            int n = io.readInt();
            for (int i = 0; i < n; i++) {
                init();
                solve(i);
                io.cache.append('\n');
            }
        }

        public void solve(int version) {
            int n = io.readInt();

            for (int i = 0, until = n + n; i < until; i++) {
                io.readString(buf, 0);
            }

            for (int i = 0; i < n; i++) {
                io.readString(buf, 0);

                Male male = males[buf[0] - 'a'];
                male.orderList.clear();
                for (int j = 0; j < n; j++) {
                    int femaleId = buf[j + 2] - 'A';
                    Female female = females[femaleId];
                    male.orderList.add(female);
                }
                male.iterator = male.orderList.iterator();
                male.version = version;
                abandon.add(male);
            }

            for (int i = 0; i < n; i++) {
                io.readString(buf, 0);

                Female female = females[buf[0] - 'A'];
                female.chooseScore = INF;
                female.choose = null;
                for (int j = 0; j < n; j++) {
                    int maleId = buf[j + 2] - 'a';
                    female.score[maleId] = j;
                }
            }

            //Stable-marriage
            //Optimal for male
            //So male choose females
            while (!abandon.isEmpty()) {
                Male head = abandon.removeFirst();
                Female female = head.iterator.next();
                if (female.score[head.id] < female.chooseScore) {
                    female.chooseScore = female.score[head.id];

                    if (female.choose != null) {
                        female.choose.female = null;
                        abandon.addLast(female.choose);
                    }

                    female.choose = head;
                    head.female = female;
                } else {
                    abandon.addLast(head);
                }
            }

            for (Male male : males) {
                if (male.version != version) {
                    continue;
                }
                io.cache.append((char) (male.id + 'a')).append(' ').append((char) (male.female.id + 'A')).append('\n');
            }
        }
    }

    public static class Male {
        int id;
        int version;
        List<Female> orderList = new ArrayList(26);
        Iterator<Female> iterator;
        Female female;

        @Override
        public String toString() {
            return Character.toString((char) ('a' + id));
        }
    }

    public static class Female {
        int id;
        int[] score = new int[26];
        Male choose;
        int chooseScore;

        @Override
        public String toString() {
            return Character.toString((char) ('A' + id));
        }
    }

    public static class FastIO {
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        public final StringBuilder cache = new StringBuilder();

        private byte[] buf = new byte[1 << 13];
        private int bufLen;
        private int bufOffset;
        private int next;

        public FastIO(InputStream is, OutputStream os, Charset charset) {
            this.is = is;
            this.os = os;
            this.charset = charset;
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
            long num = readLong();
            if (next != '.') {
                return num;
            }

            next = read();
            long divisor = 1;
            long later = 0;
            while (next >= '0' && next <= '9') {
                divisor = divisor * 10;
                later = later * 10 + next - '0';
                next = read();
            }

            if (num >= 0) {
                return num + (later / (double) divisor);
            } else {
                return num - (later / (double) divisor);
            }
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

    public static class Memory {
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
