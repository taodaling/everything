package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BZOJ1085 {
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
        BitOperator bitOperator = new BitOperator();

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            int t = io.readInt();
            while (t-- > 0)
                solve();
        }

        public void solve() {
            char[][] map = new char[5][5];
            for (int i = 0; i < 5; i++) {
                io.readString(map[i], 0);
            }

//            int status = valueOf(map);
//            if (possible.containsKey(status)) {
//                io.cache.append(possible.get(status)).append('\n');
//                return;
//            } else {
//                io.cache.append(-1).append('\n');
//                return;
//            }

            Set<Integer> dist = new HashSet(10000);
            {
                int status = valueOf(map);
                if (possible.containsKey(status)) {
                    io.cache.append(possible.get(status)).append('\n');
                    return;
                }


                deques[0].clear();
                deques[1].clear();
                deques[0].addLast(status);
                dist.add(deques[0].peekFirst());
            }

            int ans = 100;
            for (int i = 0; i <= 3; i++) {
                while (!deques[i & 1].isEmpty()) {
                    Integer status = deques[i & 1].pollFirst();
                    int board = boardOf(status);
                    int r = rowOf(status);
                    int c = columnOf(status);
                    int centerId = r * 5 + c;
                    for (int[] way : ways) {
                        int nr = r + way[0];
                        int nc = c + way[1];
                        if (nr < 0 || nr >= 5 || nc < 0 || nc >= 5) {
                            continue;
                        }
                        int whichCeil = nr * 5 + nc;
                        Integer ns = valueOf(bitOperator.setBit(bitOperator.setBit(board, centerId, bitOperator.bitAt(status, whichCeil) == 1), whichCeil, false), nr, nc);
                        if (dist.contains(ns)) {
                            continue;
                        }
                        dist.add(ns);
                        if (possible.containsKey(ns)) {
                            ans = Math.min(ans, possible.get(ns) + i + 1);
                        }
                        deques[(i & 1) ^ 1].addLast(ns);
                    }
                }
            }

            if (ans > 15) {
                io.cache.append(-1).append('\n');
            } else {
                io.cache.append(ans).append('\n');
            }
        }

        Map<Integer, Integer> possible = new HashMap(700000);
        Deque<Integer>[] deques = new ArrayDeque[2];
        int[][] ways = new int[][]{
                {2, 1},
                {2, -1},
                {-2, 1},
                {-2, -1},
                {1, 2},
                {-1, 2},
                {1, -2},
                {-1, -2}
        };

        {
            char[][] success = new char[][]{
                    {'1', '1', '1', '1', '1'},
                    {'0', '1', '1', '1', '1'},
                    {'0', '0', '*', '1', '1'},
                    {'0', '0', '0', '0', '1'},
                    {'0', '0', '0', '0', '0'}
            };


            for (int i = 0; i < 2; i++) {
                deques[i] = new ArrayDeque(1000000);
            }
            deques[0].add(valueOf(success));
            possible.put(deques[0].peekFirst(), 0);
            for (int i = 0; i <= 10; i++) {
                while (!deques[i & 1].isEmpty()) {
                    Integer status = deques[i & 1].pollFirst();
                    int board = boardOf(status);
                    int r = rowOf(status);
                    int c = columnOf(status);
                    int centerId = r * 5 + c;
                    for (int[] way : ways) {
                        int nr = r + way[0];
                        int nc = c + way[1];
                        if (nr < 0 || nr >= 5 || nc < 0 || nc >= 5) {
                            continue;
                        }
                        int whichCeil = nr * 5 + nc;
                        Integer ns = valueOf(bitOperator.setBit(bitOperator.setBit(board, centerId, bitOperator.bitAt(status, whichCeil) == 1), whichCeil, false), nr, nc);
                        if (possible.containsKey(ns)) {
                            continue;
                        }
                        possible.put(ns, i + 1);
                        deques[(i & 1) ^ 1].addLast(ns);
                    }
                }
            }
        }


        public int centerOf(int status) {
            return status >> 25;
        }

        public int rowOf(int status) {
            return centerOf(status) / 5;
        }

        public int columnOf(int status) {
            return centerOf(status) % 5;
        }

        public int boardOf(int status) {
            return status & ((1 << 25) - 1);
        }

        public int valueOf(char[][] board) {
            int r = -1;
            int c = -1;
            int b = 0;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (board[i][j] == '*') {
                        r = i;
                        c = j;
                        continue;
                    }
                    b = bitOperator.setBit(b, i * 5 + j, board[i][j] == '1');
                }
            }
            return valueOf(b, r, c);
        }

        public int valueOf(int board, int x, int y) {
            int id = x * 5 + y;
            return (id << 25) | board;
        }


    }

    /**
     * Bit operations
     */
    public static class BitOperator {
        public int bitAt(int x, int i) {
            return (x >> i) & 1;
        }

        public int bitAt(long x, int i) {
            return (int) ((x >> i) & 1);
        }

        public int setBit(int x, int i, boolean v) {
            if (v) {
                x |= 1 << i;
            } else {
                x &= ~(1 << i);
            }
            return x;
        }

        public long setBit(long x, int i, boolean v) {
            if (v) {
                x |= 1L << i;
            } else {
                x &= ~(1L << i);
            }
            return x;
        }

        /**
         * Determine whether x is subset of y
         */
        public boolean subset(long x, long y) {
            return intersect(x, y) == x;
        }

        /**
         * Merge two set
         */
        public long merge(long x, long y) {
            return x | y;
        }

        public long intersect(long x, long y) {
            return x & y;
        }

        public long differ(long x, long y) {
            return x - intersect(x, y);
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
