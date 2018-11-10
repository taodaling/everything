package com.daltao.oj.old.submit.codeforces;

import java.io.*;

/**
 * Created by dalt on 2018/4/5.
 */
public class CF926E {
    static final int INF = (int) 1e8;
    static final int MOD = (int) 1e9 + 7;
    public static BlockReader input;
    public static PrintStream output;

    public static void main(String[] args) throws FileNotFoundException {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            input = new BlockReader(new FileInputStream("D:\\DataBase\\TESTCASE\\codeforces\\CF926E.in"));
            output = System.out;
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }


        solve();

        output.flush();
    }

    public static void solve() {
        int n = input.nextInteger();
        LinkedNode[] nodes = new LinkedNode[n + 2];
        nodes[0] = new LinkedNode();
        nodes[0].val = (int) 2e9;
        nodes[0].index = 0;
        int[] diff = new int[n + 2];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new LinkedNode();
            nodes[i].val = input.nextInteger();
            nodes[i].index = i;
            nodes[i - 1].next = nodes[i];
            nodes[i].prev = nodes[i - 1];
            diff[i - 1] = Math.abs(nodes[i - 1].val - nodes[i].val);
        }
        nodes[n + 1] = new LinkedNode();
        nodes[n + 1].val = (int) 2e9;
        nodes[n + 1].index = n + 1;
        nodes[n].next = nodes[n + 1];
        nodes[n + 1].prev = nodes[n];
        diff[n] = Math.abs(nodes[n].val - nodes[n + 1].val);
        diff[n + 1] = Integer.MAX_VALUE;

        int rbound = n + 1;
        Node root = Node.makeTree(0, rbound, nodes, diff);
        while (Node.queryMinVal(root) == 0) {
            LinkedNode minNode = Node.queryMinNode(root);
            LinkedNode next = minNode.next;
            LinkedNode prev = minNode.prev;
            minNode.val++;
            Node.update(prev.index, 0, rbound, root, prev, Math.abs(prev.val - minNode.val));
            Node.update(next.index, 0, rbound, root, next, Integer.MAX_VALUE);
            minNode.next = next.next;
            minNode.next.prev = minNode;
            Node.update(minNode.index, 0, rbound, root, minNode, Math.abs(minNode.val - minNode.next.val));
        }

        int cnt = 0;
        for (LinkedNode trace = nodes[0].next, until = nodes[n + 1]; trace != until; trace = trace.next) {
            cnt++;
        }
        output.println(cnt);
        for (LinkedNode trace = nodes[0].next, until = nodes[n + 1]; trace != until; trace = trace.next) {
            output.print(trace.val);
            output.print(' ');
        }
    }

    public static class Node {
        int val;
        LinkedNode node;
        Node left, right;

        public static Node makeTree(int l, int r, LinkedNode[] initData, int[] initVal) {
            Node node = new Node();
            if (l != r) {
                int m = (l + r) >> 1;
                node.left = makeTree(l, m, initData, initVal);
                node.right = makeTree(m + 1, r, initData, initVal);
                node.pushUp();
            } else {
                node.node = initData[l];
                node.val = initVal[l];
            }
            return node;
        }

        public static void update(int x, int l, int r, Node node, LinkedNode nodeVal, int val) {
            if (l > x || x > r) {
                return;
            }
            if (l == r) {
                node.node = nodeVal;
                node.val = val;
                return;
            }
            int m = (l + r) >> 1;
            update(x, l, m, node.left, nodeVal, val);
            update(x, m + 1, r, node.right, nodeVal, val);
            node.pushUp();
        }

        public static int queryMinVal(Node node) {
            return node.val;
        }

        public static LinkedNode queryMinNode(Node node) {
            return node.node;
        }

        public void pushUp() {
            if (left.val <= right.val) {
                val = left.val;
                node = left.node;
            } else {
                val = right.val;
                node = right.node;
            }
        }
    }

    public static class LinkedNode {
        LinkedNode next;
        LinkedNode prev;
        int val;
        int index;
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
