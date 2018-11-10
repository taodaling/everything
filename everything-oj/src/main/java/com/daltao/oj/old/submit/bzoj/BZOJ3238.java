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
 * Created by dalt on 2018/2/20.
 */
public class BZOJ3238 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ3238.in"));
        } else {
            System.setOut(new PrintStream(System.out, false));
        }
        input = new BlockReader(System.in);

        char[] data = new char[1000001];
        int len = input.nextBlock(data, 0);

        SAM sam = new SAM();
        for (int i = len - 1; i >= 0; i--) {
            sam.consume(data[i]);
        }

        SAM.Node[] nodes = sam.toTree().toArray(new SAM.Node[0]);
        long prefixSum = 0;
        for (int i = nodes.length - 1; i >= 1; i--) {
            SAM.Node node = nodes[i];

            node.father.cnt += node.cnt;

            prefixSum += (long)node.cnt * (node.cnt - 1) / 2 * (node.maxlen - node.father.maxlen);
        }

        long lengthChoose = (long)(len - 1) * (1 + len) * len / 2;
        long result = lengthChoose - (prefixSum * 2);
        System.out.println(result);
    }

    public static class SAM {
        public final static Node NIL = new Node();
        Node root, buildEndPoint;
        int buildSize;
        Node matchEndPoint;

        public SAM() {
            root = new Node();
            root.father = NIL;
            matchEndPoint = root;
            buildEndPoint = root;
        }

        public void match(char c) {
            int index = c - 'a';
            if (matchEndPoint.transfer[index] != null) {
                matchEndPoint = matchEndPoint.transfer[index];
            } else {
                while (matchEndPoint != NIL && matchEndPoint.transfer[index] == null) {
                    matchEndPoint = matchEndPoint.father;
                }
                if (matchEndPoint == NIL) {
                    matchEndPoint = root;
                } else {
                    matchEndPoint = matchEndPoint.transfer[index];
                }
            }
            matchEndPoint.cnt++;
        }

        public List<Node> toTree() {
            Deque<Node> que = new ArrayDeque(2 * buildSize + 1);
            que.addLast(root);
            root.visit = true;

            List<Node> list = new ArrayList(2 * buildSize + 1);
            while (!que.isEmpty()) {
                Node head = que.removeFirst();
                list.add(head);

                for (Node next : head.transfer) {
                    if (next == null || next.visit) {
                        continue;
                    }
                    next.visit = true;
                    que.addLast(next);
                }
            }

            return list;
        }

        public void consume(char c) {
            buildSize++;
            int index = c - 'a';

            Node cur = new Node();
            cur.maxlen = buildSize;
            cur.rmax = cur.maxlen;
            cur.cnt = 1;

            Node p = buildEndPoint;
            while (p != NIL && p.transfer[index] == null) {
                p.transfer[index] = cur;
                p = p.father;
            }
            if (p == NIL) {
                cur.father = root;
            } else {
                Node q = p.transfer[index];
                if (q.maxlen == p.maxlen + 1) {
                    cur.father = q;
                } else {
                    Node clone = new Node();
                    clone.father = q.father;
                    clone.maxlen = p.maxlen + 1;
                    clone.transfer = q.transfer.clone();

                    q.father = cur.father = clone;
                    while (p != NIL && p.transfer[index] == q) {
                        p.transfer[index] = clone;
                        p = p.father;
                    }
                }
            }

            buildEndPoint = cur;
        }

        public static class Node {
            Node[] transfer = new Node[26];
            Node father;
            int maxlen;
            int rmax;

            int cnt;
            boolean visit;
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
