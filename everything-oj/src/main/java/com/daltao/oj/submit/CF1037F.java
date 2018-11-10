package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class CF1037F {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("/Users/daltao/DATABASE/TESTCASE/CF1037F.in"), System.out, charset) : new FastIO(System.in,
                System.out, charset);
        Task task = new Task(io);

        if (async) {
            Thread t = new Thread(null, task, "dalt", 1 << 27);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            t.join();
        } else {
            task.run();
        }

        if (local) {
            io.cache.append("\n\n--memory -- " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        }

        io.flush();
    }

    public static class Task implements Runnable {
        final FastIO io;
        static final int MOD = (int) (1e9 + 7);

        public Task(FastIO io) {
            this.io = io;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            int n = io.readInt();
            int k = io.readInt() - 1;
            //cache[i] means monster stay at 0, and decided to go to position i
            //Next second, monster stay at k, so the remain light number should be i - k.
            int[] cache = new int[n + 1];
            for (int i = 0; i <= n; i++) {
                if (i < k) {
                    cache[i] = 0;
                    continue;
                }
                cache[i] = (cache[i - k] + i - k) % MOD;
            }

            int[] data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = io.readInt();
            }

            int[] leftUntil = new int[n];
            int[] rightUntil = new int[n];
            IntDeque deque = new IntDeque(n);
            IntComparator comparator = (a, b) -> data[a] == data[b] ? (a - b) : (data[a] - data[b]);
            for (int i = 0; i < n; i++) {
                while (!deque.isEmpty() && comparator.compare(deque.peekLast(), i) < 0) {
                    deque.removeLast();
                }
                if (deque.isEmpty()) {
                    leftUntil[i] = 0;
                } else {
                    leftUntil[i] = deque.peekLast() + 1;
                }
                deque.addLast(i);
            }
            deque.reset();
            for (int i = n - 1; i >= 0; i--) {
                while (!deque.isEmpty() && comparator.compare(deque.peekFirst(), i) < 0) {
                    deque.removeFirst();
                }
                if (deque.isEmpty()) {
                    rightUntil[i] = n;
                } else {
                    rightUntil[i] = deque.peekFirst();
                }
                deque.addFirst(i);
            }

            long sum = 0;
            for (int i = 0; i < n; i++) {
                sum += calcTotal(rightUntil[i] - leftUntil[i], rightUntil[i] - i, k, cache) * (long) data[i] % MOD;
            }
            sum %= MOD;

            io.cache.append(sum);
        }

        //monster at pos 0, I am at pos offset, each time we move step
        //The last light is at pos length
        public static int calcTotal(int length, int offset, int step, int[] cache) {
            int step1Time = (length - offset) / step;
            int remain = length - step * step1Time;
            long sum = step1Time * offset + cache[remain] - cache[offset - 1];

            sum %= MOD;
            if (sum < 0) {
                sum += MOD;
            }
            return (int) sum;
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

            double f = readLong();
            while (f >= 100000000) {
                f /= 1000000000;
            }
            while (f >= 10000) {
                f /= 100000;
            }
            while (f >= 100) {
                f /= 1000;
            }
            while (f >= 1) {
                f /= 10;
            }
            return num > 0 ? (num + f) : (num - f);
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

    public static class IntDeque {
        int[] data;
        int bpos;
        int epos;
        int cap;

        public IntDeque(int cap) {
            this.cap = cap;
            this.data = new int[cap];
        }

        public int size() {
            return epos - bpos;
        }

        public boolean isEmpty() {
            return epos == bpos;
        }

        public int peekFirst() {
            return data[bpos];
        }

        private int last(int i) {
            return (i == 0 ? cap : i) - 1;
        }

        private int next(int i) {
            int n = i + 1;
            return n == cap ? 0 : n;
        }

        public int peekLast() {
            return data[last(epos)];
        }

        public int removeFirst() {
            int t = bpos;
            bpos = next(bpos);
            return data[t];
        }

        public int removeLast() {
            return data[epos = last(epos)];
        }

        public void addLast(int val) {
            data[epos] = val;
            epos = next(epos);
        }

        public void addFirst(int val) {
            data[bpos = last(bpos)] = val;
        }

        public void reset() {
            bpos = epos = 0;
        }
    }

    public static interface IntComparator {
        public int compare(int a, int b);
    }

}
