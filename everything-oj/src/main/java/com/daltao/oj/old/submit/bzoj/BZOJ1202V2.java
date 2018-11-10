package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/1/20.
 */
public class BZOJ1202V2 {
    public static final int INF = (int) 1e8;
    public static final int NODE_LIMIT = 100;

    public static BlockReader input;
    public static Node[] nodes = new Node[NODE_LIMIT + 1];
    public static int runtime = 1;

    static {
        for (int i = 0; i <= NODE_LIMIT; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ1202.in"));
        input = new BlockReader(System.in);
        for (int i = 1, bound = input.nextInteger(); i <= bound; i++) {
            runtime = i;
            BZOJ1202V2 solution = new BZOJ1202V2();
            System.out.println(solution.solve());
        }
    }


    public String solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();

        for (int i = 0; i <= n; i++) {
            nodes[i].init();
        }

        boolean reliable = true;
        for (int i = 1; i <= m; i++) {
            Node s = nodes[input.nextInteger() - 1];
            Node t = nodes[input.nextInteger()];

            int v = input.nextInteger();

            if (s.getRepr() == t.getRepr()) {
                if (t.sum - s.sum != v) {
                    reliable = false;
                }
            } else {
                Node.union(t, s, v);
            }
        }

        return reliable ? "true" : "false";
    }

    public static class Node {
        Node p;
        int rank;
        int sum;
        int id;

        @Override
        public String toString() {
            return "" + id + ":" + sum;
        }

        public static void union(Node a, Node b, int a2b) {
            Node atmp = a;
            Node btmp = b;
            a = a.getRepr();
            b = b.getRepr();

            a2b = btmp.sum - atmp.sum + a2b;

            if (a == b) {
                return;
            }
            if (a.rank == b.rank) {
                a.rank++;
            }
            if (a.rank > b.rank) {
                b.p = a;
                b.sum = -a2b;
            } else {
                a.p = b;
                a.sum = a2b;
            }
        }

        public void init() {
            p = this;
            rank = 0;
            sum = 0;
        }

        public Node getRepr() {
            if (p.p != p) {
                Node tmp = p;
                p = p.getRepr();
                sum += tmp.sum;
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
