package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/11.
 */
public class IstheInformationReliable {
    static final int INF = (int) 1e8;
    static BlockReader reader;
    int n;
    List<Node> nodeList;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\IstheInformationReliable.in"));

        reader = new BlockReader(System.in);
        while (reader.hasMore()) {
            IstheInformationReliable istheInformationReliable = new IstheInformationReliable();
            istheInformationReliable.init();
            System.out.println(istheInformationReliable.solve());
        }
    }

    public void buildEdge(Node from, Node to, int length) {
        Edge edge = new Edge();
        edge.src = from;
        edge.dst = to;
        edge.length = length;
        from.out.add(edge);
    }

    public void init() {
        n = reader.nextInteger();
        int m = reader.nextInteger();
        nodeList = new ArrayList(n + 1);
        Node source = new Node();
        nodeList.add(source);
        for (int i = 0; i < n; i++) {
            Node newNode = new Node();
            nodeList.add(newNode);

            buildEdge(source, newNode, 0);
        }

        for (int i = 0; i < m; i++) {
            String type = reader.nextBlock();
            int a = reader.nextInteger();
            int b = reader.nextInteger();
            if (type.charAt(0) == 'P') {
                int x = reader.nextInteger();
                buildEdge(nodeList.get(a), nodeList.get(b), -x);
                buildEdge(nodeList.get(b), nodeList.get(a), x);
            } else {
                buildEdge(nodeList.get(a), nodeList.get(b), -1);
            }
        }
    }

    public String solve() {
        LinkedList<Node> queue = new LinkedList();
        queue.addLast(nodeList.get(0));
        nodeList.get(0).distance = 0;
        nodeList.get(0).status = true;

        int limitForEachNode = nodeList.size();
        while (!queue.isEmpty()) {
            Node head = queue.removeFirst();
            head.status = false;
            if (++head.fixedTime > limitForEachNode) {
                queue.add(head);
                break;
            }
            for (Edge edge : head.out) {
                Node dst = edge.dst;
                int newDistance = head.distance + edge.length;
                if (newDistance < dst.distance) {
                    dst.distance = newDistance;
                    if (dst.status == false) {
                        dst.status = true;
                        queue.addLast(dst);
                    }
                }
            }
        }

        return queue.isEmpty() ? "Reliable" : "Unreliable";
    }


    public static class Node {
        int distance = INF;
        int fixedTime = 0;
        List<Edge> out = new ArrayList();
        boolean status;

        public String toString() {
            return "" + distance;
        }
    }

    public static class Edge {
        int length;
        Node src;
        Node dst;
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 1 << 13);
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
