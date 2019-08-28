package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class LUOGU4022 {
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


        int[] dp = new int[1100000];
        char[] s = new char[1100001];
        MinIntQueue queue = new MinIntQueue(1100000, (a, b) -> dp[a] - dp[b]);
        SuffixAutomaton automaton = new SuffixAutomaton();

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();

            for (int i = 0; i < m; i++) {
                int len = io.readString(s, 0);
                for (int j = 0; j < len; j++) {
                    automaton.build(s[j]);
                }
                automaton.build('2');
            }

            for (int j = 0; j < n; j++) {
                solveSingle();
            }
        }

        public void solveSingle() {
            int len = io.readString(s, 1);
            int l = 0;
            int r = len;
            while (l < r) {
                int m = (l + r + 1) >> 1;
                if (check(len, m)) {
                    l = m;
                } else {
                    r = m - 1;
                }
            }
            io.cache.append(l).append('\n');
        }

        public boolean check(int len, int l) {
            if (l > len) {
                return false;
            }
            int min = inf;
            automaton.beginMatch();
            queue.reset();
            dp[0] = 0;
            for (int i = 1; i < l; i++) {
                dp[i] = i;
                automaton.match(s[i]);
            }
            min = Math.min(min, dp[0] - 0);
            int next = 1;
            queue.enqueue(0);
            for (int i = l; i <= len; i++) {
                automaton.match(s[i]);
                while (!queue.isEmpty() && queue.peek() + automaton.matchLength < i) {
                    queue.deque();
                }
                dp[i] = min + i - automaton.matchLength;
                if (!queue.isEmpty()) {
                    dp[i] = Math.min(dp[i], queue.min());
                }
                dp[i] = Math.min(dp[i], dp[i - 1] + 1);

                int head = i - l + 1;
                for (; next <= head && next + automaton.matchLength <= i; next++) {
                    min = Math.min(min, dp[next] - next);
                }
                queue.enqueue(head);
            }

            return dp[len] * 10 <= len;
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

    public interface IntComparator {
        public int compare(int a, int b);
    }


    public static class MinIntQueue {
        IntDeque minQueue;
        IntDeque data;
        IntComparator comparator;

        public MinIntQueue(int cap, IntComparator comparator) {
            minQueue = new IntDeque(cap);
            data = new IntDeque(cap);
            this.comparator = comparator;
        }

        public void reset() {
            minQueue.reset();
            data.reset();
        }

        public void enqueue(int val) {
            data.addLast(val);
            while (!minQueue.isEmpty() && comparator.compare(minQueue.peekLast(), val) > 0) {
                minQueue.removeLast();
            }
            minQueue.addLast(val);
        }

        public int deque() {
            int val = data.removeFirst();
            if (minQueue.peekFirst() == val) {
                minQueue.removeFirst();
            }
            return val;
        }

        public int peek() {
            return data.peekFirst();
        }

        public int size() {
            return data.size();
        }

        public int min() {
            return minQueue.peekFirst();
        }

        public boolean isEmpty() {
            return data.isEmpty();
        }
    }


    public static class IntDeque {
        int[] data;
        int bpos;
        int epos;
        int cap;

        public IntDeque(int cap) {
            this.cap = cap + 1;
            this.data = new int[this.cap];
        }

        public int size() {
            int s = epos - bpos;
            if (s < 0) {
                s += cap;
            }
            return s;
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

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = bpos; i != epos; i = next(i)) {
                builder.append(data[i]).append(' ');
            }
            return builder.toString();
        }
    }


    public static class SuffixAutomaton {
        static final int MIN_CHARACTER = '0';
        static final int MAX_CHARACTER = '2';
        static final int RANGE_SIZE = MAX_CHARACTER - MIN_CHARACTER + 1;
        Node root;
        Node buildLast;
        Node matchLast;
        int matchLength;

        public SuffixAutomaton() {
            buildLast = root = new Node();
            root.fail = null;
        }

        public void beginMatch() {
            matchLast = root;
            matchLength = 0;
        }

        public void match(char c) {
            int index = c - MIN_CHARACTER;
            if (matchLast.next[index] != null) {
                matchLast = matchLast.next[index];
                matchLength = matchLength + 1;
                return;
            }
            while (matchLast != null && matchLast.next[index] == null) {
                matchLast = matchLast.fail;
            }
            if (matchLast == null) {
                matchLast = root;
                matchLength = 0;
            } else {
                matchLength = matchLast.maxlen + 1;
                matchLast = matchLast.next[index];
            }
        }

        public void build(char c) {
            int index = c - MIN_CHARACTER;
            Node now = new Node();
            now.maxlen = buildLast.maxlen + 1;

            Node p = visit(index, buildLast, null, now);
            if (p == null) {
                now.fail = root;
            } else {
                Node q = p.next[index];
                if (q.maxlen == p.maxlen + 1) {
                    now.fail = q;
                } else {
                    Node clone = q.clone();
                    clone.maxlen = p.maxlen + 1;

                    now.fail = q.fail = clone;
                    visit(index, p, q, clone);
                }
            }

            buildLast = now;
        }

        public Node visit(int index, Node trace, Node target, Node replacement) {
            while (trace != null && trace.next[index] == target) {
                trace.next[index] = replacement;
                trace = trace.fail;
            }
            return trace;
        }

        public static class Node implements Cloneable {
            Node[] next = new Node[RANGE_SIZE];
            Node fail;
            int maxlen;

            @Override
            public Node clone() {
                try {
                    Node res = (Node) super.clone();
                    res.next = res.next.clone();
                    return res;
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
