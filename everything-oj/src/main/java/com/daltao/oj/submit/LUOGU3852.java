package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class LUOGU3852 {
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
            ChordalGraph cg = new ChordalGraph(n);
            for (int i = 0; i < m; i++) {
                cg.addEdge(io.readInt() - 1, io.readInt() - 1);
            }
            io.cache.append(cg.maxIndependentSet().size());
        }
    }

    public static class ChordalGraph {
        public static class Node {
            Set<Node> next = new LinkedHashSet();
            int id;
            int marked = -1;
            int rank;
            int color;

            Node prev;
            Node later;
        }

        private static class LinkedList {
            Node head;

            public boolean isEmpty() {
                return head == null;
            }

            public Node pollHead() {
                Node ret = head;
                head = head.later;
                if (head != null) {
                    head.prev = null;
                }
                return ret;
            }

            public void removeNode(Node node) {
                if (node == head) {
                    pollHead();
                    return;
                }
                node.prev.later = node.later;
                if (node.later != null) {
                    node.later.prev = node.prev;
                }
                node.prev = node.later = null;
            }

            public void add(Node node) {
                node.later = head;
                if (head != null) {
                    head.prev = node;
                }
                head = node;
            }
        }

        private Node[] perfectRemoveSequence;
        private boolean isChordal;
        Node[] nodes;
        int n;

        public ChordalGraph(int n) {
            this.n = n;
            nodes = new Node[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }
        }

        public void addEdge(int a, int b) {
            if (perfectRemoveSequence != null) {
                throw new IllegalStateException();
            }
            nodes[a].next.add(nodes[b]);
            nodes[b].next.add(nodes[a]);
        }

        public boolean isChordal() {
            if (perfectRemoveSequence != null) {
                return isChordal;
            }
            perfectRemoveSequence = new Node[n];
            int perfectRemoveSequenceHead = n - 1;
            LinkedList[] lists = new LinkedList[n];
            int greatest = 0;
            for (int i = 0; i < n; i++) {
                lists[i] = new LinkedList();
            }
            for (Node node : nodes) {
                lists[0].add(node);
            }
            while (greatest >= 0) {
                if (lists[greatest].isEmpty()) {
                    greatest--;
                    continue;
                }
                Node head = lists[greatest].pollHead();
                head.marked = perfectRemoveSequenceHead;
                perfectRemoveSequence[perfectRemoveSequenceHead--] = head;
                for (Node next : head.next) {
                    if (next.marked != -1) {
                        continue;
                    }
                    lists[next.rank].removeNode(next);
                    next.rank++;
                    greatest = Math.max(greatest, next.rank);
                    lists[next.rank].add(next);
                }
            }
            for (Node node : perfectRemoveSequence) {
                Node minNode = null;
                for (Node next : node.next) {
                    if (next.marked <= node.marked) {
                        continue;
                    }
                    if (minNode == null || minNode.marked > next.marked) {
                        minNode = next;
                    }
                }
                if (minNode == null) {
                    continue;
                }
                for (Node next : node.next) {
                    if (next.marked <= node.marked || next == minNode) {
                        continue;
                    }
                    if (!minNode.next.contains(next)) {
                        isChordal = false;
                        return isChordal;
                    }
                }
            }
            isChordal = true;
            return isChordal;
        }

        private int minColorCover = -1;

        /**
         * 获取最小染色数，处理后node.color表示染色方案，从0开始。
         */
        public int minColorCover() {
            if (minColorCover != -1) {
                return minColorCover;
            }
            if (!isChordal()) {
                throw new IllegalStateException();
            }
            boolean[] occupied = new boolean[1 + n];
            for (int i = 0; i < n; i++) {
                nodes[i].color = -1;
            }
            for (int i = n - 1; i >= 0; i--) {
                Node node = perfectRemoveSequence[i];
                Arrays.fill(occupied, 0, node.next.size() + 1, false);
                for (Node next : node.next) {
                    if (next.color == -1) {
                        continue;
                    }
                    occupied[next.color] = true;
                }
                node.color = 0;
                while (occupied[node.color]) {
                    node.color++;
                }
                minColorCover = Math.max(minColorCover, node.color);
            }
            minColorCover++;
            return minColorCover;
        }

        private Set<Node> maxIndependentSet;

        /**
         * 获取最大独立集
         */
        public Set<Node> maxIndependentSet() {
            if (maxIndependentSet != null) {
                return maxIndependentSet;
            }
            if (!isChordal()) {
                throw new IllegalStateException();
            }
            maxIndependentSet = new HashSet<>(n);
            for (int i = 0; i < n; i++) {
                Node node = perfectRemoveSequence[i];
                boolean flag = true;
                for (Node next : node.next) {
                    if (maxIndependentSet.contains(next)) {
                        flag = false;
                        break;
                    }
                }
                if (flag == true) {
                    maxIndependentSet.add(node);
                }
            }
            return maxIndependentSet;
        }

        /**
         * 获取最小团覆盖，每个团中仅一个元素保存在结果中。对于每个返回集合中的元素e，其对应的团为e+N(e)，
         * N(e)表示与e相邻且在完美消除序列中编号大于e的顶点。
         */
        public Set<Node> minGroupCover() {
            return maxIndependentSet();
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
