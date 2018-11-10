package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalt on 2017/12/27.
 */
public class RebuildingRoads {
    static BlockReader input;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\test\\poj\\RebuildingRoads.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            RebuildingRoads solution = new RebuildingRoads();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    static final int INF = (int) 1e8;
    int n;
    int p;
    static int[][] dp = new int[151][151];
    Node[] nodes;

    public void init() {
        n = input.nextInteger();
        p = input.nextInteger();
        nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }
        for (int i = 1; i < n; i++) {
            int f = input.nextInteger();
            int c = input.nextInteger();
            nodes[f].children.add(nodes[c]);
            nodes[c].father = nodes[f];
        }
    }

    public int dfs(Node root) {
        int[] rootDp = dp[root.id];
        for (int i = 1; i <= n; i++) {
            rootDp[i] = INF;
        }
        int childNum = 0;
        for (Node child : root.children) {
            childNum += dfs(child);
            int[] childDp = dp[child.id];
            for (int i = n; i > 0; i--) {
                for (int j = 1; j <= i; j++) {
                    rootDp[i] = Math.min(rootDp[i], childDp[j] + rootDp[i - j]);
                }
            }
        }
        childNum++;
        rootDp[childNum] = 1;
        root.childNum = childNum;
        return childNum;
    }

    public int solve() {
        Node root = nodes[1];
        while (root.father != null) {
            root = root.father;
        }
        dfs(root);

        int min = dp[1][n - p];
        for (int i = 2; i <= n; i++) {
            if (nodes[i].childNum >= p) {
                min = Math.min(min, dp[i][nodes[i].childNum - p] + 1);
            }
        }
        return min;
    }


    public static class Node {
        Node father;
        List<Node> children = new ArrayList();
        int id;
        int childNum;
    }

    public static class BlockReader {
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        static final int EOF = -1;

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        StringBuilder builder = new StringBuilder();

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

        public BlockReader(InputStream is) {
            this(is, 1024);
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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return dBuf[dPos++];
        }
    }
}
