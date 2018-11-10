package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalt on 2017/12/28.
 */
public class ContestantsDivision {
    static BlockReader input;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\test\\poj\\ContestantsDivision.in"));

        input = new BlockReader(System.in);
        StringBuilder builder = new StringBuilder();
        int n, m;
        for (int i = 1; ; i++) {
            n = input.nextInteger();
            m = input.nextInteger();
            if (n == 0 && m == 0) {
                break;
            }
            ContestantsDivision solution = new ContestantsDivision();
            solution.init(n, m);
            builder.append("Case ").append(i).append(": ").append(solution.solve()).append('\n');
        }
        System.out.print(builder.toString());
    }

    static final int INF = (int) 1e8;
    int n;
    int m;
    Node[] nodes;
    long[] dp;

    public void init(int n, int m) {
        this.n = n;
        this.m = m;
        nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node();
            nodes[i].value = input.nextInteger();
            nodes[i].id = i;
        }

        for (int i = 0; i < m; i++) {
            int f = input.nextInteger();
            int c = input.nextInteger();
            nodes[f].children.add(nodes[c]);
            nodes[c].children.add(nodes[f]);
        }

        dp = new long[n + 1];
    }

    public long dfs(Node root, Node father) {
        long sumValue = root.value;
        for (Node node : root.children) {
            if (node == father) {
                continue;
            }
            sumValue += dfs(node, root);
        }
        dp[root.id] = sumValue;
        return sumValue;
    }

    public long solve() {
        long sumValue = dfs(nodes[1], null);
        long minDiffer = Long.MAX_VALUE;
        for (int i = 2; i <= n; i++) {
            long differ = Math.abs(sumValue - dp[nodes[i].id] * 2);
            if (differ < minDiffer) {
                minDiffer = differ;
            }
        }
        return minDiffer;
    }


    public static class Node {
        List<Node> children = new ArrayList();
        int id;
        long value;
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
