package com.daltao.template;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BZOJ1031 {
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
            String s = io.readString();
            int n = s.length();
            SuffixArray sa = new SuffixArray((s + s).toCharArray(), 0, 128);
            for (SuffixArray.Suffix suffix : sa.orderedSuffix) {
                if (suffix.suffixStartIndex >= n) {
                    continue;
                }
                int index = suffix.suffixStartIndex;
                int tail = (index + n - 1) % n;
                io.cache.append(s.charAt(tail));
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


    public static class SuffixArray {
        Suffix[] orderedSuffix;
        Suffix[] originalSuffix;
        int[] heights;
        char[] data;

        public SuffixArray(char[] s, int rangeFrom, int rangeTo) {
            this.data = s;

            int n = s.length;
            int range = n + 1;
            Loop<int[]> rankLoop = new Loop(new int[3][n + 1]);

            Suffix[] originalSuffix = new Suffix[n + 1];
            int[] firstRanks = rankLoop.get(0);
            for (int i = 0; i < n; i++) {
                originalSuffix[i] = new Suffix();
                originalSuffix[i].suffixStartIndex = i;
                firstRanks[i] = s[i] - rangeFrom + 1;
            }
            originalSuffix[n] = new Suffix();
            originalSuffix[n].suffixStartIndex = n;
            originalSuffix[n].rank = 0;
            Loop<Suffix[]> suffixLoop = new Loop(new Suffix[][]{
                    originalSuffix.clone(), new Suffix[n + 1]
            });

            sort(suffixLoop.get(0), suffixLoop.get(1), rankLoop.get(0), rangeTo - rangeFrom + 1);
            assignRank(suffixLoop.turn(), rankLoop.get(0), rankLoop.get(0), rankLoop.turn());

            for (int i = 1; i < n; i <<= 1) {
                System.arraycopy(rankLoop.get(0), i, rankLoop.get(1), 0, range - i);
                Arrays.fill(rankLoop.get(1), range - i, range, 0);
                sort(suffixLoop.get(0), suffixLoop.turn(), rankLoop.get(1), range);
                sort(suffixLoop.get(0), suffixLoop.turn(), rankLoop.get(0), range);
                assignRank(suffixLoop.get(0), rankLoop.get(0), rankLoop.get(1), rankLoop.turn(2));
            }

            firstRanks = rankLoop.get(0);
            for (int i = 0; i < range; i++) {
                originalSuffix[i].rank = firstRanks[i];
            }

            this.originalSuffix = originalSuffix;
            this.orderedSuffix = suffixLoop.get();

            heights = new int[n + 1];
            for (int i = 0; i < n; i++) {
                Suffix suffix = originalSuffix[i];
                if (suffix.rank == 0) {
                    heights[suffix.rank] = 0;
                    continue;
                }
                int startIndex = suffix.suffixStartIndex;
                int former = startIndex - 1;
                int h = 0;
                if (former >= 0) {
                    h = Math.max(h, heights[originalSuffix[former].rank] - 1);
                }
                int anotherStartIndex = orderedSuffix[suffix.rank - 1].suffixStartIndex;
                for (; startIndex + h < n && anotherStartIndex + h < n && s[startIndex + h] == s[anotherStartIndex + h]; h++)
                    ;
                heights[suffix.rank] = h;
            }
        }

        private static void assignRank(Suffix[] seq, int[] firstKeys, int[] secondKeys, int[] rankOutput) {
            int cnt = 0;
            rankOutput[0] = 0;
            for (int i = 1, bound = seq.length; i < bound; i++) {
                int lastIndex = seq[i - 1].suffixStartIndex;
                int index = seq[i].suffixStartIndex;
                if (firstKeys[lastIndex] != firstKeys[index] ||
                        secondKeys[lastIndex] != secondKeys[index]) {
                    cnt++;
                }
                rankOutput[index] = cnt;
            }
        }

        private static void sort(Suffix[] oldSeq, Suffix[] newSeq, int[] withRank, int range) {
            int[] counters = new int[range];
            for (int rank : withRank) {
                counters[rank]++;
            }
            int[] ranks = new int[range];
            ranks[0] = 0;
            for (int i = 1; i < range; i++) {
                ranks[i] = ranks[i - 1] + (counters[i] > 0 ? 1 : 0);
                counters[i] += counters[i - 1];
            }

            for (int i = oldSeq.length - 1; i >= 0; i--) {
                int newPos = --counters[withRank[oldSeq[i].suffixStartIndex]];
                newSeq[newPos] = oldSeq[i];
            }
        }

        /**
         * 获取第rank大的后缀，最小的后缀的排名为1
         */
        public Suffix getSuffixByRank(int rank) {
            return orderedSuffix[rank];
        }

        /**
         * 获取以startIndex开始的后缀对应的后缀对象
         */
        public Suffix getSuffixByStartIndex(int startIndex) {
            return originalSuffix[startIndex];
        }

        /**
         * 计算第i大的后缀和第i-1大的后缀的最长公共前缀长度
         */
        public int longestCommonPrefixOf(int i) {
            return heights[i];
        }

        private static class Loop<T> {
            T[] loops;
            int offset;

            public Loop(T[] initVal) {
                loops = initVal;
            }

            public T get(int index) {
                return loops[(offset + index) % loops.length];
            }

            public T get() {
                return get(0);
            }

            public T turn(int degree) {
                offset += degree;
                return get(0);
            }

            public T turn() {
                return turn(1);
            }
        }

        public class Suffix {
            int suffixStartIndex;
            int rank;

            @Override
            public String toString() {
                return String.valueOf(data, suffixStartIndex, data.length - suffixStartIndex);//suffixStartIndex + ":" + rank;
            }
        }
    }
}
