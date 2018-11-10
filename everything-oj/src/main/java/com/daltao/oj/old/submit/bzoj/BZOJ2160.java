package com.daltao.oj.old.submit.bzoj;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dalt on 2018/3/20.
 */
public class BZOJ2160 {
    public static final long MODULO = 19930726;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
       // System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ2160.in"));
        input = new BlockReader(System.in);

        int n = input.nextInteger();
        long k = Long.parseLong(input.nextBlock());

        char[] data = new char[n];
        int dataLen = input.nextBlock(data, 0);

        PAM pam = new PAM(dataLen);
        for (int i = 0; i < dataLen; i++) {
            pam.build(data[i]);
        }

        PAM.Node[] nodes = pam.nodeList.toArray(new PAM.Node[0]);
        long total = 0;
        for (int i = nodes.length - 1; i >= 0; i--) {
            PAM.Node node = nodes[i];
            node.fail.visitTime += node.visitTime;
        }
        int nodeLen = 0;
        for (int i = 0, bound = nodes.length; i < bound; i++) {
            if ((nodes[i].length & 1) == 1) {
                nodes[nodeLen++] = nodes[i];
                total += nodes[i].visitTime;
            }
        }

        if (total < k) {
            System.out.println("-1");
            return;
        }

        Arrays.sort(nodes, 0, nodeLen, new Comparator<PAM.Node>() {
            @Override
            public int compare(PAM.Node a, PAM.Node b) {
                return b.length - a.length;
            }
        });

        long product = 1;
        for (int i = 0; k > 0; i++) {
            int consume = (int) Math.min(nodes[i].visitTime, k);
            k -= consume;
            product = product * pow(nodes[i].length, consume) % MODULO;
        }

        System.out.println(product);
    }

    public static long pow(long x, int n) {
        if (n < 20) {
            long product = 1;
            for (int i = 0; i < n; i++) {
                product = product * x % MODULO;
            }
            return product;
        }

        long product = 1;
        int bit = 20;
        for (; (n & (1 << bit)) == 0; bit--) ;
        for (; bit >= 0; bit--) {
            product = product * product % MODULO;
            if ((n & (1 << bit)) != 0) {
                product = product * x % MODULO;
            }
        }
        return product;
    }

    public static class PAM {
        Node even;
        Node odd;
        Node last;
        char[] data;
        int top;
        List<Node> nodeList;

        public PAM(int cap) {
            data = new char[cap];
            nodeList = new ArrayList(cap);
            top = -1;

            last = odd = new Node();
            odd.length = -1;

            even = new Node();
            even.length = 0;
            even.fail = odd;
        }

        public void build(char c) {
            data[++top] = c;

            while (top - last.length <= 0) {
                last = last.fail;
            }

            while (data[top - last.length - 1] != c) {
                last = last.fail;
            }

            int index = c - 'a';
            if (last.nodes[index] != null) {
                last = last.nodes[index];
                last.visitTime++;
                return;
            }

            Node now = new Node();
            now.length = last.length + 2;
            now.visitTime = 1;
            Node failTrace = last.fail;
            while (failTrace != null && data[top - failTrace.length - 1] != c) {
                failTrace = failTrace.fail;
            }
            now.fail = failTrace == null ? even : failTrace.nodes[index];

            last.nodes[index] = now;
            last = now;
            nodeList.add(now);
        }

        static class Node {
            Node[] nodes = new Node[26];
            int length;
            Node fail;
            int visitTime;
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