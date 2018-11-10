package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dalt on 2018/1/29.
 */
public class BZOJ2286 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    static int idAllocator = 1;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ2286.in"));
        }

        StringBuilder builder = new StringBuilder();
        input = new BlockReader(System.in);
        int n = input.nextInteger();
        Node[] nodes = new Node[n + 1];
        Node[] choose = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node();
            nodes[i].index = i;
        }
        for (int i = 1; i < n; i++) {
            Node a = nodes[input.nextInteger()];
            Node b = nodes[input.nextInteger()];
            Edge edge = new Edge();
            edge.a = a;
            edge.b = b;
            edge.length = input.nextInteger();
            a.edgeList.add(edge);
            b.edgeList.add(edge);
        }

        dfs(nodes[1], null, 0, 0);

        int m = input.nextInteger();
        ArrayDeque<Node> deque = new ArrayDeque(n);
        for (int i = 1; i <= m; i++) {
            int k = input.nextInteger();
            for (int j = 0; j < k; j++) {
                choose[j] = nodes[input.nextInteger()];
            }
            Arrays.sort(choose, 0, k);
            deque.clear();
            deque.add(nodes[1]);
            nodes[1].childNeed = 0;
            for (int j = 0; j < k; j++) {
                Node node = choose[j];
                node.childNeed = 1000000000;
                node.flag = i;
                Node lca = lca(node, deque.getLast());
                if (lca.flag != i) {
                    lca.flag = i;
                    lca.childNeed = 0;
                }
                while (lca.depth < deque.getLast().depth) {
                    Node last = deque.removeLast();
                    if (lca.depth < deque.getLast().depth) {
                        last.weakFather = deque.getLast();
                    } else {
                        last.weakFather = lca;
                    }
                    last.minLength = (int) Math.min(minLength(last, last.weakFather), last.childNeed);
                    last.weakFather.childNeed += last.minLength;
                }
                if (lca != deque.getLast()) {
                    deque.addLast(lca);
                }
                deque.addLast(node);
            }
            while (deque.size() >= 2) {
                Node last = deque.removeLast();
                last.weakFather = deque.getLast();
                last.minLength = (int) Math.min(minLength(last, last.weakFather), last.childNeed);
                last.weakFather.childNeed += last.minLength;
            }

            builder.append(nodes[1].childNeed).append(System.lineSeparator());
        }

        System.out.print(builder);
    }

    public static Node lca(Node a, Node b) {
        if (a.depth < b.depth) {
            Node tmp = a;
            a = b;
            b = tmp;
        }

        int depthDiffer = a.depth - b.depth;
        for (int i = 0; depthDiffer != 0; i++) {
            int bit = 1 << i;
            if ((depthDiffer & bit) != 0) {
                depthDiffer -= bit;
                a = a.ancestors[i];
            }
        }

        for (int i = 18; i >= 0 && a != b; i--) {
            if (a.ancestors[i] != b.ancestors[i]) {
                a = a.ancestors[i];
                b = b.ancestors[i];
            }
        }

        if (a == b) {
            return a;
        }
        return a.ancestors[0];
    }

    public static int minLength(Node a, Node b) {
        int depthDiffer = a.depth - b.depth;
        int minLength = Integer.MAX_VALUE;
        for (int i = 0; depthDiffer != 0; i++) {
            int bit = 1 << i;
            if ((depthDiffer & bit) != 0) {
                depthDiffer -= bit;
                minLength = Math.min(minLength, a.minEdgeLength[i]);
                a = a.ancestors[i];
            }
        }
        return minLength;
    }

    public static void dfs(Node node, Node father, int depth, int length) {
        node.father = father;
        node.ancestors[0] = father;
        node.id = idAllocator++;
        node.minEdgeLength[0] = length;
        for (int i = 0; node.ancestors[i] != null; i++) {
            node.ancestors[i + 1] = node.ancestors[i].ancestors[i];
            node.minEdgeLength[i + 1] = Math.min(node.minEdgeLength[i], node.ancestors[i].minEdgeLength[i]);
        }
        node.depth = depth + 1;

        for (Edge edge : node.edgeList) {
            Node dst = edge.a == node ? edge.b : edge.a;
            if (dst == father) {
                continue;
            }
            dfs(dst, node, node.depth, edge.length);
        }
    }

    public static class Node implements Comparable<Node> {
        Node father;
        List<Edge> edgeList = new ArrayList(1);
        Node weakFather;
        int id;
        Node[] ancestors = new Node[19];
        int[] minEdgeLength = new int[19];
        int depth;
        int minLength;
        int flag;
        long childNeed;

        int index;

        @Override
        public String toString() {
            return "" + index;
        }

        @Override
        public int compareTo(Node o) {
            return id - o.id;
        }
    }

    public static class Edge {
        Node a;
        Node b;
        int length;

    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;

        public BlockReader(InputStream is) {
            this(is, 4096);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
        }

        public int nextInteger() {
            skipBlank();
            int ret = 0;
            boolean rev = false;
            if (next == '+' || next == '-') {
                rev = next == '-';
                next = nextByte();
            }
            while (next >= '0' && next <= '9') {
                ret = (ret << 3) + (ret << 1) + next - '0';
                next = nextByte();
            }
            return rev ? -ret : ret;
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public int nextBlock(char[] data, int offset) {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() {
            skipBlank();
            return next != EOF;
        }
    }
}
