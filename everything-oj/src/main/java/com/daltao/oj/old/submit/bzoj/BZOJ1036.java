package com.daltao.oj.old.submit.bzoj;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dalt on 2018/1/2.
 */
public class BZOJ1036 {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        //System.setIn(new FileInputStream("D:\\test\\bzoj\\BZOJ1036.in"));

        input = new BlockReader(System.in);

        BZOJ1036 solution = new BZOJ1036();
        System.out.print(solution.solve());
    }

    public String solve() {
        StringBuilder result = new StringBuilder();

        int nodeNum = input.nextInteger();
        SNode[] nodes = new SNode[nodeNum + 1];
        for (int i = 1; i <= nodeNum; i++) {
            nodes[i] = new SNode();
            nodes[i].id = i;
        }
        for (int i = 1; i < nodeNum; i++) {
            SNode a = nodes[input.nextInteger()];
            SNode b = nodes[input.nextInteger()];
            a.join(b);
        }

        for (int i = 1; i <= nodeNum; i++) {
            nodes[i].changeValue(input.nextInteger());
        }

        int questionNum = input.nextInteger();
        char[] cmd = new char[10];
        for (int i = 0; i < questionNum; i++) {
            input.nextBlock(cmd, 0);
            int a = input.nextInteger();
            int b = input.nextInteger();
            if (cmd[1] == 'H') {
                nodes[a].changeValue(b);
            } else if (cmd[1] == 'S') {
                nodes[a].asRootSrcTo(nodes[b]);
                int sum = nodes[a].sum;
                result.append(sum).append('\n');
            } else {
                nodes[a].asRootSrcTo(nodes[b]);
                int max = nodes[a].max;
                result.append(max).append('\n');
            }
        }
        return result.toString();
    }

    public static class SNode {
        public static final SNode NIL = new SNode();
        int id;

        static {
            NIL.max = Integer.MIN_VALUE;
            NIL.sum = 0;
        }

        @Override
        public String toString() {
            return id + ":" + value;
        }

        SNode left = NIL;
        SNode right = NIL;
        SNode father = NIL;
        SNode treeFather = NIL;
        boolean revFlag;
        int value;
        int max;
        int sum;

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

        public void makeRoot() {
            access();
            splay(this);
            this.reverse();
        }

        public void changeValue(int value) {
            splay(this);
            this.value = value;
            update();
        }

        public void join(SNode f) {
            makeRoot();
            treeFather = f;
        }

        public void asRootSrcTo(SNode target) {
            makeRoot();
            target.access();
            splay(this);
        }

        public void reverse() {
            revFlag = !revFlag;
        }

        public void asLeft(SNode x) {
            left = x;
            x.father = this;
        }

        public void asRight(SNode x) {
            right = x;
            x.father = this;
        }

        public void replaceChild(SNode x, SNode y) {
            if (left == x) {
                asLeft(y);
            } else {
                asRight(y);
            }
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

        public static void splay(SNode x) {
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

        public void update() {
            sum = value + left.sum + right.sum;
            max = Math.max(Math.max(left.max, right.max), value);
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
