package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by dalt on 2018/1/26.
 */
public class BZOJ2007 {
    public static final int INF = 100000000;
    public static BlockReader input;
    public static int vertexNum;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ2007.in"));

        input = new BlockReader(System.in);

        int n = input.nextInteger();

        Node[][] joints = new Node[n + 2][n + 2];
        for (int i = 0, bound = n + 2; i < bound; i++) {
            for (int j = 0; j < bound; j++) {
                joints[i][j] = new Node();
                joints[i][j].row = i;
                joints[i][j].col = j;
            }
        }


//e->w
        for (int i = 0; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                int num = input.nextInteger();
                buildEdge(joints[i + 1][j], joints[i][j], num);
            }
        }


        //s->n
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                int num = input.nextInteger();
                buildEdge(joints[i][j], joints[i][j + 1], num);
            }
        }

        //w->e
        for (int i = 0; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                int num = input.nextInteger();
                buildEdge(joints[i][j], joints[i + 1][j], num);
            }
        }

        //n->s
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                int num = input.nextInteger();
                buildEdge(joints[i][j + 1], joints[i][j], num);
            }
        }


        Node src = new Node();
        src.row = 10000;
        Node dst = new Node();
        dst.col = 10000;
        //top
        for (int i = 1; i <= n; i++) {
            buildEdge(joints[0][i], joints[0][i + 1], 0);
            buildEdge(joints[0][i + 1], joints[0][i], 0);
            buildEdge(joints[0][i], dst, 0);
        }
        buildEdge(joints[0][n + 1], dst, 0);
        //bottom
        for (int i = 0; i < n; i++) {
            buildEdge(joints[n + 1][i], joints[n + 1][i + 1], 0);
            buildEdge(joints[n + 1][i + 1], joints[n + 1][i], 0);
            buildEdge(src, joints[n + 1][i], 0);
        }
        buildEdge(src, joints[n + 1][n], 0);
        //left
        for (int i = 1; i <= n; i++) {
            buildEdge(joints[i][0], joints[i + 1][0], 0);
            buildEdge(joints[i + 1][0], joints[i][0], 0);
            buildEdge(src, joints[i][0], 0);
        }
        buildEdge(src, joints[n + 1][0], 0);
        //right
        for (int i = 0; i < n; i++) {
            buildEdge(joints[i][n + 1], joints[i + 1][n + 1], 0);
            buildEdge(joints[i + 1][n + 1], joints[i][n + 1], 0);
            buildEdge(joints[i][n + 1], dst, 0);
        }
        buildEdge(joints[n][n + 1], dst, 0);

        //spfa
        Deque<Node> deque = new ArrayDeque((n + 2) * (n + 2));
        deque.addLast(src);
        src.d = 0;
        src.inque = true;
        while (!deque.isEmpty()) {
            Node head = deque.removeFirst();
            head.inque = false;
            for (Edge edge : head.edgeList) {
                Node next = edge.dst;
                if (next.d > head.d + edge.len) {
                    next.d = head.d + edge.len;
                    if (!next.inque) {
                        next.inque = true;
                        deque.addLast(next);
                    }
                }
            }
        }

        System.out.println(dst.d);
    }

    public static void buildEdge(Node src, Node dst, int len) {
        Edge edge = new Edge();
        edge.dst = dst;
        edge.len = len;
        src.edgeList.add(edge);
    }

    public static class Edge {
        Node dst;
        int len;

        @Override
        public String toString() {
            return dst.toString() + ":" + len;
        }
    }

    public static class Node {
        List<Edge> edgeList = new ArrayList();
        int row, col;
        int d = INF;
        boolean inque;

        @Override
        public String toString() {
            return String.format("(%d,%d):%d", row, col, d);
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

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

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
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
    }
}
