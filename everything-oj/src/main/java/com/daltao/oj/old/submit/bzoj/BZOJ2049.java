package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dalt on 2018/1/9.
 */
public class BZOJ2049 {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ2049.in"));
        input = new BlockReader(System.in);
        while (input.hasMore()) {
            BZOJ2049 solution = new BZOJ2049();
            System.out.print(solution.solve());
        }
    }

    public String solve() {
        StringBuilder result = new StringBuilder();
        int n = input.nextInteger();
        int m = input.nextInteger();

        SNode[] nodes = new SNode[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new SNode();
            nodes[i].id = i;
        }
        char[] cmd = new char[20];
        for (int i = 0; i < m; i++) {
            input.nextBlock(cmd, 0);
            SNode a = nodes[input.nextInteger()];
            SNode b = nodes[input.nextInteger()];
            switch (cmd[0]) {
                case 'C': {
                    a.join(b);
                    break;
                }
                case 'D': {
                    a.cut(b);
                    break;
                }
                case 'Q': {
                    boolean inSameTree = a.getRoot() == b.getRoot();
                    result.append(inSameTree ? "Yes\n" : "No\n");
                    break;
                }
            }
        }
        return result.toString();
    }

    public static class SNode {
        private static final SNode NIL = new SNode();

        static {
            NIL.left = NIL.right = NIL.father = NIL.treeFather = NIL;
        }

        SNode left = NIL, right = NIL, father = NIL;
        SNode treeFather = NIL;
        boolean revFlag;
        int id;

        public static void zig(SNode x) {
            SNode y = x.father;
            SNode z = y.father;
            SNode b = x.right;

            z.replaceChild(y, x);
            x.asRight(y);
            y.asLeft(b);

            y.update();
        }

        public static void zag(SNode x) {
            SNode y = x.father;
            SNode z = y.father;
            SNode b = x.left;

            z.replaceChild(y, x);
            x.asLeft(y);
            y.asRight(b);

            y.update();
        }

        @Override
        public String toString() {
            return "" + id;
        }

        public SNode getRoot() {
            access();
            splay(this);
            SNode x = this;
            while (x.left != NIL) {
                x = x.left;
                x.pushDown();
            }
            splay(x);
            return x;
        }

        public void access() {
            SNode x = this;
            SNode y = NIL;
            while (x != NIL) {
                splay(x);
                x.right.treeFather = x;
                x.right.father = NIL;
                x.asRight(y);
                x.update();
                y = x;
                x = x.treeFather;
            }
        }

        public void asRoot() {
            access();
            splay(this);
            reverse();
        }

        public void join(SNode x) {
            asRoot();
            treeFather = x;
        }

        public void cut(SNode x) {
            x.asRoot();
            access();
            splay(this);
            left.father = NIL;
            asLeft(NIL);
            update();
        }

        public void splay(SNode x) {
            if (x == NIL) {
                return;
            }
            SNode y, z;
            while ((y = x.father) != NIL) {
                if ((z = y.father) == NIL) {
                    y.pushDown();
                    x.pushDown();
                    if (x == y.left) {
                        zig(x);
                    } else {
                        zag(x);
                    }
                } else {
                    z.pushDown();
                    y.pushDown();
                    x.pushDown();
                    if (x == y.left) {
                        if (y == z.left) {
                            zig(y);
                            zig(x);
                        } else {
                            zig(x);
                            zag(x);
                        }
                    } else {
                        if (y == z.left) {
                            zag(x);
                            zig(x);
                        } else {
                            zag(y);
                            zag(x);
                        }
                    }
                }
            }
            x.pushDown();
            x.update();
        }

        public void replaceChild(SNode x, SNode y) {
            if (left == x) {
                asLeft(y);
            } else {
                asRight(y);
            }
        }

        public void asLeft(SNode x) {
            left = x;
            x.father = this;
        }

        public void asRight(SNode x) {
            right = x;
            x.father = this;
        }

        public void reverse() {
            revFlag = !revFlag;
        }

        public void pushDown() {
            if (revFlag) {
                revFlag = false;
                left.reverse();
                right.reverse();

                SNode tmp = left;
                left = right;
                right = tmp;
            }

            left.treeFather = treeFather;
            right.treeFather = treeFather;
        }

        public void update() {
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