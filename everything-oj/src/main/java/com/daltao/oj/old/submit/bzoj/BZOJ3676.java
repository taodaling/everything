package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/25.
 */
public class BZOJ3676 {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ3676.in"));
        input = new BlockReader(System.in);

        char[] data = new char[300005];
        int dataLen = input.nextBlock(data, 0);

        PAM pam = new PAM(data, dataLen);
        for (int i = 0; i < dataLen; i++) {
            pam.build(i);
        }
        long result = 0;
        for (int i = pam.nodeList.size() - 1; i >= 0; i--) {
            PAM.Node node = pam.nodeList.get(i);
            node.fail.visitCnt += node.visitCnt;
            result = Math.max(result, (long) node.visitCnt * node.length);
        }
        System.out.println(result);
    }

    public static class PAM {
        Node last;
        Node odd;
        Node even;
        char[] data;
        List<Node> nodeList;

        public PAM(char[] data, int dataLen) {
            odd = new Node();
            odd.length = -1;
            even = new Node();
            even.length = 0;
            even.fail = odd;

            last = odd;
            this.data = data;
            nodeList = new ArrayList(dataLen);
        }

        public void build(int i) {
            int index = data[i] - 'a';
            while (i - last.length - 1 < 0) {
                last = last.fail;
            }
            while (data[i - last.length - 1] != data[i]) {
                last = last.fail;
            }
            if (last.nodes[index] != null) {
                last = last.nodes[index];
                last.visitCnt++;
                return;
            }
            Node now = new Node();
            now.length = last.length + 2;
            now.visitCnt = 1;
            last.nodes[index] = now;
            last = last.fail;
            while (last != null && data[i - last.length - 1] != data[i]) {
                last = last.fail;
            }
            if (last == null) {
                now.fail = even;
            } else {
                now.fail = last.nodes[index];
            }

            last = now;
            nodeList.add(now);
        }

        public static class Node {
            Node[] nodes = new Node[26];
            Node fail;
            int visitCnt;
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
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
        }
    }
}