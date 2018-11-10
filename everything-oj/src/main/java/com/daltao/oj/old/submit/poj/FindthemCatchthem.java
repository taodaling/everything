package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/23.
 */
public class FindthemCatchthem {
    static final String IN_SAME_GANG = "In the same gang.\n";
    static final String IN_DIFFERENT_GANGS = "In different gangs.\n";
    static final String NOT_SURE_YET = "Not sure yet.\n";
    static BlockReader input;
    static Element[] elements = new Element[100001];

    static {
        for (int i = 0, bound = elements.length; i < bound; i++) {
            elements[i] = new Element();
        }
    }

    static StringBuilder builder = new StringBuilder();

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\FindthemCatchthem.in"));

        input = new BlockReader(System.in);
        int testCase = input.nextInteger();
        while (testCase-- > 0) {
            FindthemCatchthem solution = new FindthemCatchthem();
            solution.init();
            System.out.print(solution.solve());
        }
    }

    public void init() {
    }

    public String solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();
        for (int i = 1; i <= n; i++) {
            elements[i].p = elements[i];
            elements[i].differ = null;
            elements[i].rank = 1;
        }
        char[] cmd = new char[1];
        for (int i = 0; i < m; i++) {
            input.nextBlock(cmd, 0);
            Element a = elements[input.nextInteger()].getRepr();
            Element b = elements[input.nextInteger()].getRepr();
            switch (cmd[0]) {
                case 'A': {
                    if (a == b) {
                        builder.append(IN_SAME_GANG);
                    } else if (a.isDiffer(b)) {
                        builder.append(IN_DIFFERENT_GANGS);
                    } else {
                        builder.append(NOT_SURE_YET);
                    }
                    break;
                }
                case 'D': {
                    Element.setDiffer(a, b);
                    break;
                }
            }
        }
        String result = builder.toString();
        builder.setLength(0);
        return result;
    }

    public static class Element {
        Element p = this;
        int rank;
        Element differ;


        public static void union(Element a, Element b) {
            a = a.getRepr();
            b = b.getRepr();
            if (a.rank == b.rank) {
                if (a == b) {
                    return;
                }
                a.rank++;
            }
            if (a.rank > b.rank) {
                b.p = a;
            } else {
                a.p = b;
            }
            if (a.differ == null || b.differ == null) {
                a.getRepr().differ = a.differ == null ? b.differ : a.differ;
            } else {
                union(a.differ, b.differ);
            }
        }

        public static void setDiffer(Element a, Element b) {
            a = a.getRepr();
            b = b.getRepr();
            if (a.differ == null) {
                a.differ = b;
            } else {
                union(a.differ, b);
            }

            if (b.differ == null) {
                b.differ = a;
            } else {
                union(b.differ, a);
            }
        }

        public Element getRepr() {
            return p == p.p ? p : (p = p.getRepr());
        }

        public boolean isDiffer(Element other) {
            Element d = getRepr().differ;
            return d != null && d.getRepr() == other.getRepr();
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
