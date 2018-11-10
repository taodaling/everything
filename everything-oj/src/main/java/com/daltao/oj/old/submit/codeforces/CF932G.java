package com.daltao.oj.old.submit.codeforces;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/3/1.
 */
public class CF932G {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static int MODULO = 1000000007;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\codeforces\\CF932G.in"));
        }
        input = new BlockReader(System.in);

        solve();
    }

    public static void solve() {
        char[] data = new char[1000000];
        int dataLen = input.nextBlock(data, 0);
        PAM pam = new PAM(dataLen);
        int halfLen = dataLen / 2;
        for (int i = 0; i < halfLen; i++) {
            pam.build(data[i]);
            pam.build(data[dataLen - i - 1]);
        }

        System.out.println(pam.ans[dataLen]);
    }

    public static class PAM {
        char[] data;
        int top;
        int[] ans;
        Node even;
        Node odd;
        Node last;

        public PAM(int cap) {
            data = new char[cap + 1];
            top = 0;
            ans = new int[cap + 1];
            ans[0] = 1;

            odd = new Node();
            odd.length = -1;
            even = new Node();
            even.length = 0;
            even.fail = odd;

            last = odd;
        }

        public Node lsp(Node node) {
            while (data[top - node.length - 1] != data[top]) {
                node = node.fail;
            }
            return node;
        }

        public void build(char c) {
            int index = c - 'a';
            data[++top] = c;
            last = lsp(last);
            if (last.next[index] == null) {
                Node now = new Node();
                now.length = last.length + 2;
                if (now.length == 1) {
                    now.fail = even;
                } else {
                    now.fail = lsp(last.fail).next[index];
                }
                now.update();
                last.next[index] = now;
            }
            last = last.next[index];
            after();
        }

        public int findJoint(Node node) {
            node.dp = ans[top - node.link.length - node.differ];
            if (node.link != node.fail) {
                node.dp = (node.dp + node.fail.dp) % MODULO;
            }
            return node.dp;
        }

        public void after() {
            int sum = 0;
            for (Node trace = last; trace != even; trace = trace.link) {
                sum = (sum + findJoint(trace)) % MODULO;
            }
            ans[top] = sum;
            if ((top & 1) == 1) {
                ans[top] = 0;
            }
        }

        public static class Node {
            Node[] next = new Node[26];
            Node fail;
            int length;
            Node link;
            int differ;
            int dp;

            public void update() {
                differ = length - fail.length;
                if (differ != fail.differ) {
                    link = fail;
                } else {
                    link = fail.link;
                }
            }
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
