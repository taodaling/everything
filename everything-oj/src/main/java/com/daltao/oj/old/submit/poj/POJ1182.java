package com.daltao.oj.old.submit.poj;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dalt on 2018/1/19.
 */
public class POJ1182 {
    public static BlockReader input;
    static Node[] nodes;


    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\test\\poj\\POJ1182.in"));
        input = new BlockReader(System.in);
        while (input.hasMore()) {
            POJ1182 solution = new POJ1182();
            //   solution.before();
            System.out.println(solution.solve());
        }
    }

    public int solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();
        nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }

        int falseCnt = 0;
        for (int i = 0; i < m; i++) {
            int type = input.nextInteger();
            int x = input.nextInteger();
            int y = input.nextInteger();

            if (x > n || y > n || x <= 0 || y <= 0) {
                falseCnt++;
            } else {

                Node xNode = nodes[x];
                Node yNode = nodes[y];

                if (xNode.getRepr() != yNode.getRepr()) {
                    if (type == 1) {
                        Node.union(xNode, yNode, 0);
                    } else {
                        Node.union(xNode, yNode, 1);
                    }
                } else {
                    int distance = ((Node.getDistancec(xNode, yNode) % 3) + 3) % 3;
                    if (type == 1) {
                        if (distance != 0) {
                            falseCnt++;
                        }
                    } else {
                        if (distance != 1) {
                            falseCnt++;
                        }
                    }
                }
            }
        }

        return falseCnt;
    }

    public static class Node {
        Node p = this;
        int rank = 0;
        int id;

        @Override
        public String toString() {
            return "" + id;
        }

        int relativeDistance = 0;

        public static int getDistancec(Node a, Node b) {
            return a.relativeDistance - b.relativeDistance;
        }

        public static void union(Node a, Node b, int a2b) {
            a2b = a2b + b.relativeDistance - a.relativeDistance;

            a = a.getRepr();
            b = b.getRepr();


            if (a == b) {
                return;
            }
            if (a.rank == b.rank) {
                a.rank++;
            }
            if (a.rank > b.rank) {
                b.p = a;
                b.relativeDistance = -a2b;
            } else {
                a.p = b;
                a.relativeDistance = a2b;
            }
        }

        public Node getRepr() {
            if (p != p.p) {
                Node tmp = p;
                p = p.getRepr();
                relativeDistance += tmp.relativeDistance;
            }
            return p;
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
