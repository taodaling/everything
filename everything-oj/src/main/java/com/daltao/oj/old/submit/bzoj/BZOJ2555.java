package com.daltao.oj.old.submit.bzoj;

import java.io.*;

/**
 * Created by Administrator on 2018/1/27.
 */
public class BZOJ2555 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ2555.in"));
        } else {
            System.setOut(new PrintStream(System.out, false));
        }
        input = new BlockReader(System.in);

        int mask = 0;
        int q = input.nextInteger();
        char[] data = new char[600000];
        int len;

        len = input.nextBlock(data, 0);
        SAM sam = new SAM();
        for (int i = 0; i < len; i++) {
            sam.consume(data[i]);
        }


        char[] cmd = new char[16];
        for (int i = 0; i < q; i++) {
            input.nextBlock(cmd, 0);
            len = input.nextBlock(data, 0);
            decodeByMask(data, len, mask);

            if (cmd[0] == 'Q') {
                int val = sam.match(data, len);
                mask ^= val;
                System.out.println(val);
            } else {
                for (int j = 0; j < len; j++) {
                    sam.consume(data[j]);
                }
            }
        }
    }

    public static void decodeByMask(char[] data, int len, int mask) {
        char tmp;
        for (int j = 0; j < len; j++) {
            mask = (mask * 131 + j) % len;

            tmp = data[j];
            data[j] = data[mask];
            data[mask] = tmp;
        }
    }

    public static class SAM {
        LCTNode root;
        LCTNode buildEndPoint;
        LCTNode matchEndPoint;
        int size;

        public SAM() {
            root = buildEndPoint = new LCTNode();
            root.treeFather = LCTNode.NIL;
        }

        public void consume(int c) {
            c -= 'A';
            LCTNode cur = new LCTNode();
            cur.maxlen = ++size;

            LCTNode p = buildEndPoint;
            while (p != LCTNode.NIL && p.transfer[c] == null) {
                p.transfer[c] = cur;
                p = p.treeFather;
            }
            if (p == LCTNode.NIL) {
                cur.treeFather = root;
            } else {
                LCTNode q = p.transfer[c];
                if (q.maxlen == p.maxlen + 1) {
                    cur.treeFather = q;
                } else {
                    LCTNode.cut(q, q.treeFather);
                    LCTNode clone = new LCTNode(q);
                    clone.maxlen = p.maxlen + 1;
                    cur.treeFather = q.treeFather = clone;
                    LCTNode.join(q, clone);

                    while (p != LCTNode.NIL && p.transfer[c] == q) {
                        p.transfer[c] = clone;
                        p = p.treeFather;
                    }

                    if (p == LCTNode.NIL) {
                        LCTNode.join(clone, root);
                    } else {
                        LCTNode.join(clone, p.transfer[c]);
                    }
                }
            }

            LCTNode.join(cur, cur.treeFather);
            LCTNode.getRoute(cur, root).batchUpdate(1);
            buildEndPoint = cur;
        }

        public int match(char[] s, int len) {
            matchEndPoint = root;
            for (int i = 0; i < len && matchEndPoint != null; i++) {
                int c = s[i] - 'A';
                matchEndPoint = matchEndPoint.transfer[c];
            }

            if (matchEndPoint == null) {
                return 0;
            }
            LCTNode.splay(matchEndPoint);
            return matchEndPoint.val;
        }
    }

    public static class LCTNode implements Cloneable {
        public static LCTNode NIL = new LCTNode();
        LCTNode father;
        LCTNode forestFather;
        LCTNode left;
        LCTNode right;
        boolean rev;
        int val;
        int batchUpdate;
        int maxlen;

        LCTNode treeFather;
        LCTNode[] transfer = new LCTNode[2];

        public LCTNode() {
            father = NIL;
            forestFather = NIL;
            left = NIL;
            right = NIL;
        }

        public LCTNode(LCTNode node) {
            this();
            val = node.val;
            treeFather = node.treeFather;
            transfer = new LCTNode[]{node.transfer[0], node.transfer[1]};
        }

        public static void join(LCTNode c, LCTNode f) {
            makeRoot(c);
            c.forestFather = f;
        }

        public static void cut(LCTNode c, LCTNode f) {
            makeRoot(f);
            access(c);
            splay(f);
            f.right.father = NIL;
            f.asRight(NIL);
        }

        public static LCTNode getRoute(LCTNode a, LCTNode b) {
            makeRoot(a);
            return access(b);
        }

        public static void zig(LCTNode x) {
            LCTNode y = x.father;
            LCTNode z = y.father;
            LCTNode b = x.right;

            z.changeChild(y, x);
            x.asRight(y);
            y.asLeft(b);

            y.pushUp();
        }

        public static void zag(LCTNode x) {
            LCTNode y = x.father;
            LCTNode z = y.father;
            LCTNode b = x.left;

            z.changeChild(y, x);
            x.asLeft(y);
            y.asRight(b);

            y.pushDown();
        }

        public static void splay(LCTNode x) {
            if (x == NIL) {
                return;
            }
            LCTNode y, z;
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
            x.pushUp();
        }

        public static LCTNode access(LCTNode x) {
            LCTNode last = NIL;
            while (x != NIL) {
                splay(x);
                x.right.forestFather = x;
                x.right.father = NIL;
                x.asRight(last);

                last = x;
                x = x.forestFather;
            }
            return last;
        }

        public static void makeRoot(LCTNode x) {
            access(x);
            splay(x);
            x.reverse();
        }

        public void pushUp() {

        }

        public void reverse() {
            rev = !rev;
        }

        public void batchUpdate(int v) {
            val += v;
            batchUpdate += v;
        }

        public void pushDown() {
            if (rev) {
                rev = false;
                LCTNode tmp = left;
                left = right;
                right = tmp;

                left.reverse();
                right.reverse();
            }

            if (batchUpdate != 0) {
                left.batchUpdate(batchUpdate);
                right.batchUpdate(batchUpdate);
                batchUpdate = 0;
            }

            left.forestFather = right.forestFather = forestFather;
        }

        public void asLeft(LCTNode x) {
            left = x;
            x.father = this;
        }

        public void asRight(LCTNode x) {
            right = x;
            x.father = this;
        }

        public void changeChild(LCTNode former, LCTNode later) {
            if (left == former) {
                asLeft(later);
            } else {
                asRight(later);
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
