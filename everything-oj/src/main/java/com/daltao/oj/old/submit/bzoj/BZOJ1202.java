package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Created by Administrator on 2018/1/20.
 */
public class BZOJ1202 {
    public static final int INF = (int) 1e8;
    public static final int NODE_LIMIT = 101;

    public static BlockReader input;
    public static Node[] nodes = new Node[NODE_LIMIT + 1];
    public static Edge[][] edges = new Edge[NODE_LIMIT + 1][NODE_LIMIT + 1];
    public static int runtime = 1;

    static {
        for (int i = 0; i <= NODE_LIMIT; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }

        for (int i = 0; i <= NODE_LIMIT; i++) {
            for (int j = 0; j <= NODE_LIMIT; j++) {
                edges[i][j] = new Edge();
                edges[i][j].dst = nodes[j];
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ1202.in"));
        input = new BlockReader(System.in);
        for (int i = 1, bound = input.nextInteger(); i <= bound; i++) {
            runtime = i;
            BZOJ1202 solution = new BZOJ1202();
            System.out.println(solution.solve());
        }
    }


    public String solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();

        for (int i = 0; i <= n; i++) {
            edges[NODE_LIMIT][i].existFlag = runtime;
            nodes[i].distance = INF;
            nodes[i].inque = false;
            nodes[i].cnt = 0;
        }

        for (int i = 1; i <= m; i++) {
            int s = input.nextInteger() - 1;
            int t = input.nextInteger();
            int v = input.nextInteger();

            edges[s][t].weight = v;
            edges[s][t].existFlag = runtime;
            edges[t][s].weight = -v;
            edges[t][s].existFlag = runtime;
        }

        //spfa used to find negative loop in graph
        LinkedList<Node> queue = new LinkedList();
        queue.addFirst(nodes[NODE_LIMIT]);
        nodes[NODE_LIMIT].distance = 0;
        nodes[NODE_LIMIT].inque = true;
        nodes[NODE_LIMIT].cnt = 0;
        int vertexNum = m + 2;
        boolean containNegativeLoop = false;
        while (!queue.isEmpty()) {
            Node head = queue.removeFirst();
            head.cnt++;
            head.inque = false;
            if (head.cnt >= vertexNum) {
                containNegativeLoop = true;
                break;
            }

            for (int i = 0; i <= n; i++) {
                Edge edge = edges[head.id][i];
                if (edge.existFlag != runtime) {
                    continue;
                }

                Node target = edge.dst;
                int newDist = edge.weight + head.distance;
                if (newDist < target.distance) {
                    target.distance = newDist;
                    if (!target.inque) {
                        queue.addFirst(target);
                        target.inque = true;
                    }
                }
            }
        }

        return containNegativeLoop ? "false" : "true";
    }

    public static class Node {
        int id;
        int distance;
        boolean inque;
        int cnt;

        @Override
        public String toString() {
            return "" + id + ":" + distance;
        }
    }

    public static class Edge {
        Node dst;

        int weight;
        int existFlag;
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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return dBuf[dPos++];
        }
    }
}
