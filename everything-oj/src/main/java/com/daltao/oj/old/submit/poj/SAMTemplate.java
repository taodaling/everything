package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dalt on 2018/2/20.
 */
public class SAMTemplate {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ2774.in"));
        }
        input = new BlockReader(System.in);

        char[] data = new char[100000];
        while (input.hasMore()) {
            int len = input.nextBlock(data, 0);
            SAM sam = new SAM();
            for (int i = 0; i < len; i++) {
                sam.consume(data[i]);
            }

            len = input.nextBlock(data, 0);
            for (int i = 0; i < len; i++) {
                sam.match(data[i]);
            }

            System.out.println(sam.maxMatchLength);
        }
    }

    public static class SAM {
        Node root;
        Node buildEndPoint;
        Node matchEndPoint;
        int size;
        int matchLength;
        int maxMatchLength;

        public SAM() {
            root = new Node();
            buildEndPoint = root;
            matchEndPoint = root;
        }

        public void consume(char c) {
            size++;

            int index = c - 'a';
            Node cur = new Node();
            cur.maxLen = size;

            Node p = buildEndPoint;
            while (p != null && p.solidLinks[index] == null) {
                p.solidLinks[index] = cur;
                p = p.softLink;
            }

            if (p == null) {
                cur.softLink = root;
            } else {
                Node q = p.solidLinks[index];

                if (q.maxLen == p.maxLen + 1) {
                    cur.softLink = q;
                } else {
                    Node clone = new Node();
                    clone.softLink = q.softLink;
                    clone.maxLen = p.maxLen + 1;
                    clone.solidLinks = q.solidLinks.clone();

                    p.solidLinks[index] = clone;
                    cur.softLink = clone;
                    q.softLink = clone;

                    for (p = p.softLink; p != null && p.solidLinks[index] == q; p = p.softLink) {
                        p.solidLinks[index] = clone;
                    }
                }
            }

            buildEndPoint = cur;
        }

        public void match(char c) {
            int index = c - 'a';

            if (matchEndPoint.solidLinks[index] != null) {
                matchEndPoint = matchEndPoint.solidLinks[index];
                matchLength++;
            } else {
                while (matchEndPoint != null && matchEndPoint.solidLinks[index] == null) {
                    matchEndPoint = matchEndPoint.softLink;
                }

                if (matchEndPoint == null) {
                    matchLength = 0;
                    matchEndPoint = root;
                } else {
                    matchLength = matchEndPoint.maxLen + 1;
                    matchEndPoint = matchEndPoint.solidLinks[index];
                }
            }

            maxMatchLength = Math.max(maxMatchLength, matchLength);
        }

        public static class Node {
            Node softLink;
            Node[] solidLinks = new Node[26];
            int maxLen;
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
