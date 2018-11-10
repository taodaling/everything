package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by Administrator on 2018/1/28.
 */
public class BZOJ3238V2 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ3238.in"));
        } else {
            System.setOut(new PrintStream(System.out, false));
        }
        input = new BlockReader(System.in);
        char[] cmd = new char[500000];

        int len = input.nextBlock(cmd, 0);
        //reverse the cmd and build the sam
        SAM sam = new SAM();
        for (int i = len - 1; i >= 0; i--) {
            sam.consume(cmd[i]);
        }
        for (int i = len - 1; i >= 0; i--) {
            sam.match(cmd[i]);
        }

        Node root = sam.root;
        //reset the maxlen
        List<Node> nodeList = new ArrayList();
        Deque<Node> que = new ArrayDeque(len * 2);
        que.addLast(root);
        root.visitFlag = true;
        while (!que.isEmpty()) {
            Node head = que.removeFirst();
            nodeList.add(head);
            for (Node transfer : head.transfer) {
                if (transfer == null || transfer.visitFlag) {
                    continue;
                }
                que.add(transfer);
                transfer.visitFlag = true;
            }
        }

        Node[] nodes = nodeList.toArray(new Node[nodeList.size()]);
        long sum = 0;
        for (int i = nodes.length - 1; i >= 0; i--) {
            Node node = nodes[i];
            node.father.cnt += node.cnt;
            long c2 = (long) node.cnt * (node.cnt - 1) / 2;
            sum += c2 * (node.maxlen - node.father.maxlen);
        }

        long allPossibleLength = (long) (len + 1) * len * (len - 1) / 2;
        System.out.println(allPossibleLength - 2 * sum);
    }

    public static class SAM {
        private static Node NIL = new Node();
        Node root;
        Node buildEndPoint;
        Node matchEndPoint;
        int size;

        public SAM() {
            root = buildEndPoint = matchEndPoint = new Node();
            root.father = NIL;
        }

        public void match(int c) {
            c -= 'a';
            while (matchEndPoint != NIL && matchEndPoint.transfer[c] == null) {
                matchEndPoint = matchEndPoint.father;
            }
            if (matchEndPoint == NIL) {
                matchEndPoint = root;
            } else {
                matchEndPoint = matchEndPoint.transfer[c];
            }
            matchEndPoint.cnt++;
        }

        public void consume(int c) {
            c -= 'a';
            Node cur = new Node();
            cur.maxlen = ++size;
            cur.right = cur.maxlen;

            Node p = buildEndPoint;
            while (p != NIL && p.transfer[c] == null) {
                p.transfer[c] = cur;
                p = p.father;
            }

            if (p == NIL) {
                cur.father = root;
            } else {
                Node q = p.transfer[c];
                if (q.maxlen == p.maxlen + 1) {
                    cur.father = q;
                } else {
                    Node clone = new Node();
                    clone.transfer = q.transfer.clone();
                    clone.father = q.father;
                    clone.maxlen = p.maxlen +  1;

                    q.father = cur.father = clone;
                    while (p != NIL && p.transfer[c] == q) {
                        p.transfer[c] = clone;
                        p = p.father;
                    }
                }
            }

            buildEndPoint = cur;
        }
    }

    public static class Node {
        Node[] transfer = new Node[26];
        Node father;
        int maxlen;
        int right;
        boolean visitFlag;
        int cnt;
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
