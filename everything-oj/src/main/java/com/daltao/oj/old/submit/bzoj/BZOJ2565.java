package com.daltao.oj.old.submit.bzoj;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dalt on 2018/3/20.
 */
public class BZOJ2565 {

    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        //System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ2565.in"));
        input = new BlockReader(System.in);

        char[] data = new char[100000];
        int dataLen = input.nextBlock(data, 0);

        PAM ordinal = new PAM(dataLen);
        PAM reverse = new PAM(dataLen);

        for (int i = 0; i < dataLen; i++) {
            ordinal.build(data[i]);
            reverse.build(data[dataLen - i - 1]);
        }

        int max = 2;
        for (int i = 0, bound = dataLen - 1; i < bound; i++) {
            int next = dataLen - 2 - i;
            max = Math.max(max, ordinal.dp[i] + reverse.dp[next]);
        }

        System.out.println(max);
    }

    public static class PAM {
        Node even;
        Node odd;
        char[] data;
        int dataTop;
        Node last;
        int[] dp;

        public PAM(int cap) {
            data = new char[cap];
            dataTop = -1;
            dp = new int[cap];

            odd = new Node();
            odd.length = -1;
            even = new Node();
            even.length = 0;
            even.fail = odd;

            last = odd;
        }

        public void build(char c) {
            data[++dataTop] = c;
            int index = c - 'a';

            while (dataTop - last.length - 1 < 0) {
                last = last.fail;
            }

            while (data[dataTop - last.length - 1] != c) {
                last = last.fail;
            }

            if (last.nodes[index] != null) {
                last = last.nodes[index];
                dp[dataTop] = last.length;
                return;
            }

            Node now = new Node();
            now.length = last.length + 2;
            last.nodes[index] = now;
            Node failTrace = last.fail;
            while (failTrace != null && data[dataTop - failTrace.length - 1] != c) {
                failTrace = failTrace.fail;
            }
            if (failTrace == null) {
                now.fail = even;
            } else {
                now.fail = failTrace.nodes[index];
            }

            last = now;
            dp[dataTop] = now.length;
        }

        static class Node {
            Node[] nodes = new Node[26];
            Node fail;
            int length;
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
