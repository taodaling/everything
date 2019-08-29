package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.TreeSet;

public class LUOGU4121 {
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

            Board b1 = new Board(n);
            Board b2 = new Board(n);
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    int v = io.readInt();
                    b1.setBoard(i, j, v == 1);
                    b2.setBoard(i, j, v == 0);
                }
            }

            int m = io.readInt();
            Query[] queries = new Query[m];
            for (int i = 0; i < m; i++) {
                queries[i] = new Query();
                queries[i].r = io.readInt();
                queries[i].c = io.readInt();
            }
            b2.process(queries);
            for (Query q : queries) {
                q.ans2 = q.ans1;
            }
            b1.process(queries);
            for (Query q : queries) {
                io.cache.append(q.ans1).append(' ')
                        .append(q.ans2).append('\n');
            }
        }
    }

    public static class Query {
        int r;
        int c;
        int ans1;
        int ans2;
    }

    public static class Board {
        boolean[] board;
        int n;

        private int idOf(int i, int j) {
            return i * (n + 2) + j;
        }

        public Board(int n) {
            this.n = n;
            board = new boolean[(n + 2) * (n + 2)];
        }

        public void setBoard(int i, int j, boolean v) {
            board[idOf(i, j)] = v;
        }

        int[][] ways = new int[][]{
                {0, 1},
                {1, 0},
                {0, -1},
                {-1, 0}
        };

        public void turnOffCell(boolean[] board, int[] timestamp, int i, int j, List<Edge> edges, int now) {
            int cellId = idOf(i, j);
            for (int[] way : ways) {
                int nextCellId = idOf(i + way[0], j + way[1]);
                if (board[nextCellId]) {
                    edges.add(Edge.newEdge(cellId, nextCellId, Math.max(timestamp[cellId], timestamp[nextCellId]),
                            now));
                }
            }
        }

        public void process(Query[] queries) {
            boolean[] board = this.board.clone();

            int[] timestamp = new int[(n + 2) * (n + 2)];

            List<Edge> edges = new ArrayList(4 * queries.length);
            ConnectionChecker connectionChecker = new ConnectionChecker((n + 2) * (n + 2));

            for (int i = 0; i < queries.length; i++) {
                Query q = queries[i];
                int cellId = idOf(q.r, q.c);
                if (board[cellId]) {
                    turnOffCell(board, timestamp, q.r, q.c, edges, i);
                } else {
                }
                board[cellId] = !board[cellId];
                timestamp[cellId] = i;
            }

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    int cellId = idOf(i, j);
                    if (!board[cellId]) {
                        continue;
                    }
                    turnOffCell(board, timestamp, i, j, edges, queries.length);
                    board[cellId] = !board[cellId];
                }
            }

            System.arraycopy(this.board, 0, board, 0, board.length);
            int blackCount = 0;
            for (boolean b : board) {
                if (!b) {
                    blackCount++;
                }
            }

            Collections.sort(edges, new Comparator<Edge>() {
                @Override
                public int compare(Edge o1, Edge o2) {
                    return o1.birth - o2.birth;
                }
            });
            Deque<Edge> deque = new ArrayDeque(edges);
            for (int i = 0; i < queries.length; i++) {
                Query q = queries[i];
                int cellId = idOf(q.r, q.c);
                connectionChecker.elapse(i);
                while (!deque.isEmpty() && deque.peekFirst().birth <= i) {
                    Edge edge = deque.removeFirst();
                    connectionChecker.addEdge(edge.a, edge.b, edge.die);
                }
                if (board[cellId]) {
                    blackCount++;
                } else {
                    blackCount--;
                }
                board[cellId] = !board[cellId];
                q.ans1 = connectionChecker.howManyConnectedComponentExists() - blackCount;
            }
        }
    }

    public static class Edge {
        int a;
        int b;
        int birth;
        int die;

        public static Edge newEdge(int a, int b, int birth, int die) {
            Edge edge = new Edge();
            edge.a = a;
            edge.b = b;
            edge.birth = birth;
            edge.die = die;
            return edge;
        }
    }


    public static class ConnectionChecker {
        private LCTNode[] nodes;
        private int time = -1;
        private int edgeIdAllocator = 0;
        private TreeSet<LCTNode> edges = new TreeSet(new Comparator<LCTNode>() {
            @Override
            public int compare(LCTNode a, LCTNode b) {
                return a.dieTime == b.dieTime ?
                        a.id - b.id : a.dieTime - b.dieTime;
            }
        });


        public int howManyConnectedComponentExists() {
            while (!edges.isEmpty() && edges.first().dieTime <= time) {
                edges.pollFirst();
            }
            return nodes.length - edges.size();
        }

        public ConnectionChecker(int n) {
            nodes = new LCTNode[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new LCTNode();
                nodes[i].id = i;
                nodes[i].dieTime = Integer.MAX_VALUE;
                nodes[i].pushUp();
            }
            for (int i = 1; i < n; i++) {
                LCTNode node = new LCTNode();
                node.dieTime = time;
                node.a = nodes[i - 1];
                node.b = nodes[i];
                node.pushUp();
                LCTNode.join(node.a, node);
                LCTNode.join(node.b, node);
            }
        }

        /**
         * 增加一条有效期截止到dieTime的边
         */
        public void addEdge(int aId, int bId, int dieTime) {
            LCTNode a = nodes[aId];
            LCTNode b = nodes[bId];
            LCTNode.findRoute(a, b);
            LCTNode.splay(a);
            if (a.eldest.dieTime >= dieTime) {
                return;
            }
            LCTNode eldest = a.eldest;
            LCTNode.splay(eldest);
            LCTNode.cut(eldest.a, eldest);
            LCTNode.cut(eldest.b, eldest);
            edges.remove(eldest);

            LCTNode node = new LCTNode();
            node.id = edgeIdAllocator++;
            node.dieTime = dieTime;
            node.a = a;
            node.b = b;
            node.pushUp();
            LCTNode.join(node.a, node);
            LCTNode.join(node.b, node);
            edges.add(node);
        }

        /**
         * 检查两个顶点之间是否存在一条路径
         */
        public boolean check(int aId, int bId) {
            LCTNode a = nodes[aId];
            LCTNode b = nodes[bId];
            LCTNode.findRoute(a, b);
            LCTNode.splay(b);
            return b.eldest.dieTime > time;
        }


        public void elapse(int t) {
            time = t;
        }

        private static class LCTNode {
            public static final LCTNode NIL = new LCTNode();

            static {
                NIL.left = NIL;
                NIL.right = NIL;
                NIL.father = NIL;
                NIL.treeFather = NIL;
                NIL.dieTime = Integer.MAX_VALUE;
                NIL.eldest = NIL;
            }

            LCTNode left = NIL;
            LCTNode right = NIL;
            LCTNode father = NIL;
            LCTNode treeFather = NIL;
            boolean reverse;
            int id;

            LCTNode a;
            LCTNode b;
            LCTNode eldest;
            int dieTime;

            public static LCTNode elder(LCTNode a, LCTNode b) {
                return a.dieTime < b.dieTime ? a : b;
            }

            public static void access(LCTNode x) {
                LCTNode last = NIL;
                while (x != NIL) {
                    splay(x);
                    x.right.father = NIL;
                    x.right.treeFather = x;
                    x.setRight(last);
                    x.pushUp();

                    last = x;
                    x = x.treeFather;
                }
            }

            public static void makeRoot(LCTNode x) {
                access(x);
                splay(x);
                x.reverse();
            }

            public static void cut(LCTNode y, LCTNode x) {
                makeRoot(y);
                access(x);
                splay(y);
                y.right.treeFather = NIL;
                y.right.father = NIL;
                y.setRight(NIL);
                y.pushUp();
            }

            public static void join(LCTNode y, LCTNode x) {
                makeRoot(x);
                x.treeFather = y;
            }

            public static void findRoute(LCTNode x, LCTNode y) {
                makeRoot(y);
                access(x);
            }

            public static void splay(LCTNode x) {
                if (x == NIL) {
                    return;
                }
                LCTNode y, z;
                while ((y = x.father) != NIL) {
                    if ((z = y.father) == NIL) {
                        y.pushDown();
                        x.pushDown();
                        if (x == y.left) {
                            zig(x);
                        } else {
                            zag(x);
                        }
                    } else {
                        z.pushDown();
                        y.pushDown();
                        x.pushDown();
                        if (x == y.left) {
                            if (y == z.left) {
                                zig(y);
                                zig(x);
                            } else {
                                zig(x);
                                zag(x);
                            }
                        } else {
                            if (y == z.left) {
                                zag(x);
                                zig(x);
                            } else {
                                zag(y);
                                zag(x);
                            }
                        }
                    }
                }

                x.pushDown();
                x.pushUp();
            }

            public static void zig(LCTNode x) {
                LCTNode y = x.father;
                LCTNode z = y.father;
                LCTNode b = x.right;

                y.setLeft(b);
                x.setRight(y);
                z.changeChild(y, x);

                y.pushUp();
            }

            public static void zag(LCTNode x) {
                LCTNode y = x.father;
                LCTNode z = y.father;
                LCTNode b = x.left;

                y.setRight(b);
                x.setLeft(y);
                z.changeChild(y, x);

                y.pushUp();
            }

            public static LCTNode findRoot(LCTNode x) {
                x.pushDown();
                while (x.left != NIL) {
                    x = x.left;
                    x.pushDown();
                }
                splay(x);
                return x;
            }

            @Override
            public String toString() {
                return "" + id;
            }

            public void pushDown() {
                if (reverse) {
                    reverse = false;

                    LCTNode tmpNode = left;
                    left = right;
                    right = tmpNode;

                    left.reverse();
                    right.reverse();
                }

                left.treeFather = treeFather;
                right.treeFather = treeFather;
            }

            public void reverse() {
                reverse = !reverse;
            }

            public void setLeft(LCTNode x) {
                left = x;
                x.father = this;
            }

            public void setRight(LCTNode x) {
                right = x;
                x.father = this;
            }

            public void changeChild(LCTNode y, LCTNode x) {
                if (left == y) {
                    setLeft(x);
                } else {
                    setRight(x);
                }
            }

            public void pushUp() {
                eldest = elder(this, left.eldest);
                eldest = elder(eldest, right.eldest);
            }
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
