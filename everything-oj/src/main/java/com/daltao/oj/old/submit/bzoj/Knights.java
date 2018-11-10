package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/30.
 */
public class Knights {
    static final int INF = (int) 1e8;
    static final int MAX_KNIGHTS = 1000000;
    static BlockReader input;
    Node[] nodes;

//    static {
//        for (int i = 1; i <= MAX_KNIGHTS; i++) {
//            Interval node = new Interval();
//            node.id = i;
//            nodes[i] = node;
//        }
//    }

    int n;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\Knights.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            Knights solution = new Knights();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public void init() {
        n = input.nextInteger();
        nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            Node node = new Node();
            nodes[i] = node;
        }
        for (int i = 1; i <= n; i++) {
            Node node = nodes[i];
            node.power = input.nextInteger();
            Node hate = nodes[input.nextInteger()];
            node.neighborhood.add(hate);
            hate.neighborhood.add(node);
        }
    }

    public Node[] dfs(Node node, Node father) {
        node.visited = true;
        for (Node neighbor : node.neighborhood) {
            if (neighbor == father) {
                continue;
            }
            if (neighbor.visited) {
                //Find a loop contains node and neighbor
                return new Node[]{node, neighbor};
            }
            Node[] result = dfs(neighbor, node);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void dp(Node node, Node father) {
        node.visited = true;
        long dpContain = 0;
        long dpNotContain = 0;
        for (Node neighbor : node.neighborhood) {
            if (neighbor == father) {
                continue;
            }
            dp(neighbor, node);
            dpContain += neighbor.dpNotContain;
            dpNotContain += Math.max(neighbor.dpContain, neighbor.dpNotContain);
        }
        node.dpContain = dpContain + node.power;
        node.dpNotContain = dpNotContain;
    }

    public long solve() {
        long dpSum = 0;
        for (int i = 1; i <= n; i++) {
            Node node = nodes[i];
            if (node.visited) {
                continue;
            }
            Node[] loop = dfs(node, null);
            if (loop != null) {
                long max = 0;
                loop[0].neighborhood.remove(loop[1]);
                loop[1].neighborhood.remove(loop[0]);
                dp(loop[0], null);
                max = Math.max(max, loop[0].dpNotContain);
                dp(loop[1], null);
                max = Math.max(max, loop[1].dpNotContain);
                dpSum += max;
            } else {
                dp(node, null);
                long max = Math.max(node.dpContain, node.dpNotContain);
                dpSum += max;
            }
        }
        return dpSum;
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

    public static class Node {
        int id;
        long power;
        boolean visited;
        long dpContain = 0;
        long dpNotContain = 0;
        List<Node> neighborhood = new ArrayList();

        @Override
        public String toString() {
            return id + ":" + power;
        }
    }
}
