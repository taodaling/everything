package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/24.
 */
public class PaidRoads {
    static final int INF = 100000000;
    static BlockReader input;
    int n;
    int m;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\PaidRoads.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            PaidRoads solution = new PaidRoads();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public static boolean containBit(int bits, int bit) {
        return (bits & (1 << bit)) != 0;
    }

    public String solve() {
        n = input.nextInteger();
        m = input.nextInteger();

        int[][] edges = new int[m][5];

        for (int i = 0; i < m; i++) {
            edges[i][0] = input.nextInteger() - 1;
            edges[i][1] = input.nextInteger() - 1;
            edges[i][2] = input.nextInteger() - 1;
            edges[i][3] = input.nextInteger();
            edges[i][4] = input.nextInteger();
        }

        //Bfs determine whether a road exists from src to dst
        {
            boolean[] visited = new boolean[n];
            visited[0] = true;
            LinkedList<Integer> queue = new LinkedList();
            queue.add(0);
            while (!queue.isEmpty()) {
                int head = queue.removeFirst();
                for (int i = 0; i < m; i++) {
                    if (edges[i][0] != head) {
                        continue;
                    }
                    if (visited[edges[i][1]]) {
                        continue;
                    }
                    visited[edges[i][1]] = true;
                    queue.addLast(edges[i][1]);
                }
            }

            if (!visited[n - 1]) {
                return "impossible";
            }
        }

        int stateNum = 1 << n;
        Node[][] nodeMat = new Node[stateNum][n];
        for (int i = 0; i < stateNum; i++) {
            for (int j = 0; j < n; j++) {
                Node newNode = new Node();
                nodeMat[i][j] = newNode;
            }
        }

        for (int i = 0; i < stateNum; i++) {
            for (int[] edge : edges) {
                if (!containBit(i, edge[0])) {
                    continue;
                }
                int length = containBit(i, edge[2]) ? edge[3] : edge[4];
                Node from = nodeMat[i][edge[0]];
                Node to = nodeMat[i | (1 << edge[1])][edge[1]];

                from.edgeList.add(new Edge(from, to, length));
            }
        }

        Node sink = new Node();
        Node target = new Node();
        sink.edgeList.add(new Edge(sink, nodeMat[1][0], 0));
        for (int i = 1 << (n - 1); i < stateNum; i++) {
            for (int j = 0; j < n; j++) {
                nodeMat[i][j].edgeList.add(new Edge(nodeMat[i][j], target, 0));
            }
        }

        //ISAP
        {
            LinkedList<Node> queue = new LinkedList();
            queue.addLast(sink);
            sink.inQueue = true;
            sink.dist = 0;
            while (!queue.isEmpty()) {
                Node head = queue.removeFirst();
                head.inQueue = false;
                for (Edge edge : head.edgeList) {
                    Node dst = edge.dst;
                    int newDist = edge.length + head.dist;
                    if (newDist >= dst.dist) {
                        continue;
                    }
                    dst.dist = newDist;
                    if (!dst.inQueue) {
                        dst.inQueue = true;
                        queue.add(dst);
                    }
                }
            }

            return Integer.valueOf(target.dist).toString();
        }
    }


    public void init() {

    }

    static class Node {
        List<Edge> edgeList = new ArrayList();
        boolean inQueue = false;
        int dist = INF;
    }

    static class Edge {
        Node src;
        Node dst;
        int length;

        public Edge(Node src, Node dst, int length) {
            this.src = src;
            this.dst = dst;
            this.length = length;
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 8192);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
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

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (Throwable e) {
                    dSize = -1;
                }
            }
            return dBuf[dPos++];
        }
    }
}
