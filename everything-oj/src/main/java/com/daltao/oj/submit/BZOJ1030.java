package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class BZOJ1030 {
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
        Modular modular = new Modular(10007);
        Power power = new Power(modular);

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
            ACAutomaton automaton = new ACAutomaton();
            for (int i = 0; i < n; i++) {
                String word = io.readString();
                automaton.beginBuild();
                for (char c : word.toCharArray()) {
                    c = Character.toLowerCase(c);
                    automaton.build(c);
                }
                automaton.buildLast.word = true;
            }
            automaton.endBuild();

            int statusNum = automaton.allNodes.size();
            int[][] dp = new int[m + 1][statusNum];
            dp[0][0] = 1;
            for (int i = 0; i <= m; i++) {
                for (int j = 0; j < statusNum; j++) {
                    ACAutomaton.Node node = automaton.allNodes.get(j);
                    if (node.word) {
                        dp[i][j] = 0;
                    }
                    if (dp[i][j] == 0 || i == m) {
                        continue;
                    }
                    for (int k = 0; k < ACAutomaton.RANGE_SIZE; k++) {
                        ACAutomaton.Node next = node.next[k];
                        dp[i + 1][next.id] = modular.plus(dp[i][j], dp[i + 1][next.id]);
                    }
                }
            }

            int total = 0;
            for (int i = 0; i < statusNum; i++) {
                total = modular.plus(total, dp[m][i]);
            }

            int allPossibleWays = power.pow('z' - 'a' + 1, m);
            total = modular.plus(allPossibleWays, -total);

            io.cache.append(total);
        }
    }

    /**
     * 幂运算
     */
    public static class Power {
        final Modular modular;

        public Power(Modular modular) {
            this.modular = modular;
        }

        public int pow(int x, long n) {
            if (n == 0) {
                return 1;
            }
            long r = pow(x, n >> 1);
            r = modular.valueOf(r * r);
            if ((n & 1) == 1) {
                r = modular.valueOf(r * x);
            }
            return (int) r;
        }

        public int inverse(int x) {
            return pow(x, modular.m - 2);
        }

        public int pow2(int x) {
            return x * x;
        }

        public long pow2(long x) {
            return x * x;
        }

        public double pow2(double x) {
            return x * x;
        }
    }

    /**
     * 模运算
     */
    public static class Modular {
        int m;

        public Modular(int m) {
            this.m = m;
        }

        public int valueOf(int x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return x;
        }

        public int valueOf(long x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return (int) x;
        }

        public int mul(int x, int y) {
            return valueOf((long) x * y);
        }

        public int plus(int x, int y) {
            return valueOf(x + y);
        }

        @Override
        public String toString() {
            return "mod " + m;
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

    public static class ACAutomaton {
        static final int MIN_CHARACTER = 'a';
        static final int MAX_CHARACTER = 'z';
        static final int RANGE_SIZE = MAX_CHARACTER - MIN_CHARACTER + 1;
        Node root;
        Node buildLast;
        Node matchLast;
        List<Node> allNodes = new ArrayList(1);

        private Node addNode() {
            Node node = new Node();
            node.id = allNodes.size();
            allNodes.add(node);
            return node;
        }

        public ACAutomaton() {
            root = addNode();
        }

        public void beginBuild() {
            buildLast = root;
        }

        public void endBuild() {
            Deque<Node> deque = new ArrayDeque(allNodes.size());
            for (int i = 0; i < RANGE_SIZE; i++) {
                if (root.next[i] != null) {
                    deque.addLast(root.next[i]);
                }
            }

            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                Node fail = visit(head.father.fail, head.index);
                if (fail == null) {
                    head.fail = root;
                } else {
                    head.fail = fail.next[head.index];
                }
                head.word = head.word || head.fail.word;
                for (int i = 0; i < RANGE_SIZE; i++) {
                    if (head.next[i] != null) {
                        deque.addLast(head.next[i]);
                    }
                }
            }

            for (int i = 0; i < RANGE_SIZE; i++) {
                if (root.next[i] != null) {
                    deque.addLast(root.next[i]);
                } else {
                    root.next[i] = root;
                }
            }
            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                for (int i = 0; i < RANGE_SIZE; i++) {
                    if (head.next[i] != null) {
                        deque.addLast(head.next[i]);
                    } else {
                        head.next[i] = head.fail.next[i];
                    }
                }
            }
        }

        public Node visit(Node trace, int index) {
            while (trace != null && trace.next[index] == null) {
                trace = trace.fail;
            }
            return trace;
        }

        public void build(char c) {
            int index = c - MIN_CHARACTER;
            if (buildLast.next[index] == null) {
                Node node = addNode();
                node.father = buildLast;
                node.index = index;
                buildLast.next[index] = node;
            }
            buildLast = buildLast.next[index];
        }

        public void beginMatch() {
            matchLast = root;
        }

        public void match(char c) {
            int index = c - MIN_CHARACTER;
            matchLast = matchLast.next[index];
        }

        public static class Node {
            Node[] next = new Node[RANGE_SIZE];
            Node fail;
            Node father;
            int index;
            int id;
            boolean word;

            @Override
            public String toString() {
                return father == null ? "" : (father.toString() + (char) (MIN_CHARACTER + index));
            }
        }
    }
}
