package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dalt on 2018/1/9.
 */
public class BZOJ2959 {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\test\\bzoj\\BZOJ2959.in"));

        input = new BlockReader(System.in);

        BZOJ2959 solution = new BZOJ2959();
        //   solution.before();
        System.out.print(solution.solve());
    }

    public String solve() {
        StringBuilder result = new StringBuilder();
        int n = input.nextInteger();
        int m = input.nextInteger();

        LCTNode[] nodes = new LCTNode[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new LCTNode();
        }
        char[] cmd = new char[20];
        for (int i = 0; i < m; i++) {
            input.nextBlock(cmd, 0);
            int a = input.nextInteger();
            int b = input.nextInteger();
            switch (cmd[0]) {
                case 'C': {
                    LCTNode.link(nodes[a], nodes[b]);
                    break;
                }
                case 'D': {
                    LCTNode.cut(nodes[a], nodes[b]);
                    break;
                }
                case 'Q': {
                    boolean inSameTree = LCTNode.findRoot(nodes[a]) == LCTNode.findRoot(nodes[b]);
                    result.append(inSameTree ? "YES\n" : "NO\n");
                    //System.out.println(inSameTree ? "YES\n" : "NO\n");
                    break;
                }
            }
        }
        return result.toString();
    }


    public static class LCTNode {
        static final LCTNode NIL = new LCTNode();
        SNode snode = new SNode();

        {
            snode.fatherVal = NIL;
            snode.val = this;
        }

        public static void access(LCTNode x) {
            x.split();
            while (x.snode.fatherVal != NIL) {
                LCTNode father = x.snode.fatherVal;
                father.split();
                father.snode.linkRight(x.snode);
                x = father;
            }
        }

        public static void makeRoot(LCTNode x) {
            access(x);
            x.snode.splay();
            x.snode.reverse();
        }

        public static LCTNode findRoot(LCTNode x) {
            access(x);
            x.snode.splay();
            return x.snode.getMin().val;
        }

        public static LCTNode lca(LCTNode x, LCTNode y) {
            makeRoot(x);
            access(y);
            x.snode.splay();
            return x;
        }

        public static void link(LCTNode a, LCTNode b) {
            makeRoot(a);
            a.snode.fatherVal = b;
        }

        public static void cut(LCTNode a, LCTNode b) {
            makeRoot(a);
            access(b);
            a.split();
            b.snode.fatherVal = NIL;
        }

        public void split() {
            snode.splay();
            snode.consume();
            if (snode.right != SNode.NIL) {
                snode.right.fatherVal = this;
                snode.right.removeFather();
                snode.linkRight(SNode.NIL);
            }
        }
    }

    public static class SNode {
        static final SNode NIL = new SNode();

        static {
            NIL.left = NIL.right = NIL.father = NIL;
        }

        SNode left = NIL;
        SNode right = NIL;
        SNode father = NIL;
        boolean revFlag;
        LCTNode val;
        LCTNode fatherVal;

        public void removeFather() {
            father = NIL;
        }

        //Fetch the min element (include this) in the subtree of this
        public SNode getMin() {
            SNode x = this;
            while (x.left != NIL) {
                x = x.left;
            }
            x.splay();
            return x;
        }

        public static void zig(SNode x) {
            SNode y = x.father;
            SNode z = y.father;
            SNode b = x.right;
            y.linkLeft(b);
            x.linkRight(y);
            z.replaceChild(y, x);

            y.update();
        }

        public static void zag(SNode x) {
            SNode y = x.father;
            SNode z = y.father;
            SNode b = x.left;
            y.linkRight(b);
            x.linkLeft(y);
            z.replaceChild(y, x);

            y.update();
        }

        public void splay() {
            if (this == NIL) {
                return;
            }
            SNode grandfather;
            while (father != NIL) {
                grandfather = father.father;
                if (grandfather == NIL) {
                    father.consume();
                    this.consume();
                    if (this == father.left) {
                        zig(this);
                    } else {
                        zag(this);
                    }
                } else {
                    grandfather.consume();
                    father.consume();
                    this.consume();
                    if (this == father.left) {
                        if (father == grandfather.left) {
                            zig(father);
                            zig(this);
                        } else {
                            zig(this);
                            zag(this);
                        }
                    } else {
                        if (father == grandfather.left) {
                            zag(this);
                            zig(this);
                        } else {
                            zag(father);
                            zag(this);
                        }
                    }
                }
            }
            this.update();
        }

        public void linkLeft(SNode x) {
            x.father = this;
            left = x;
        }

        public void linkRight(SNode x) {
            x.father = this;
            right = x;
        }

        public void replaceChild(SNode oldChild, SNode newChild) {
            if (left == oldChild) {
                linkLeft(newChild);
            } else {
                linkRight(newChild);
            }
        }

        public void reverse() {
            revFlag = !revFlag;
        }

        //Be invoked when ancestors changed
        public void consume() {
            if (revFlag) {
                revFlag = false;
                left.reverse();
                right.reverse();

                SNode tmp = left;
                left = right;
                right = tmp;
            }

            left.fatherVal = fatherVal;
            right.fatherVal = fatherVal;
        }

        //Be invoked when children changed
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
