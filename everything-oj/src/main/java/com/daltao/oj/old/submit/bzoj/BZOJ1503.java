package com.daltao.oj.old.submit.bzoj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/30.
 */
public class BZOJ1503 {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        //System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ1503.in"));
        input = new BlockReader(System.in);
        BZOJ1503 solution = new BZOJ1503();
        System.out.print(solution.solve());
    }

    public String solve() {
        SplayTree tree = new SplayTree();
        int n = input.nextInteger();
        int min = input.nextInteger();
        int k;
        int fix = 0;
        int totalEmploy = 0;
        char[] cmd = new char[1];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            input.nextBlock(cmd, 0);
            k = input.nextInteger();
            switch (cmd[0]) {
                case 'I': {
                    if (k >= min) {
                        totalEmploy++;
                        tree.insert(k - fix);
                    }
                    break;
                }
                case 'A': {
                    fix += k;
                    break;
                }
                case 'S': {
                    fix -= k;
                    tree.removeLessThan(min - fix);
                    break;
                }
                case 'F': {
                    int salary = tree.find(k);
                    if (salary != Integer.MIN_VALUE) {
                        salary += fix;
                    } else {
                        salary = -1;
                    }
                    builder.append(salary).append('\n');
                    break;
                }
            }
        }

        builder.append(totalEmploy - tree.size()).append('\n');
        return builder.toString();
    }


    public static class SplayTree {
        final Node NIL = new Node();
        Node root = NIL;

        {
            NIL.rank = 0;
            NIL.father = NIL;
            NIL.left = NIL.right = NIL;
        }

        public int size() {
            return root.rank;
        }

        public int find(int k) {
            if (k > root.rank) {
                return Integer.MIN_VALUE;
            }
            Node trace = root;
            while (true) {
                if (trace.right.rank >= k) {
                    trace = trace.right;
                } else {
                    k -= trace.right.rank + 1;
                    if (k == 0) {
                        break;
                    } else {
                        trace = trace.left;
                    }
                }
            }

            splay(trace);
            return trace.val;
        }

        public void removeLessThan(int val) {
            insert(val);
            root = root.right;
            root.father = NIL;
        }

        public void insert(int val) {
            Node trace = root;
            Node father = NIL;
            while (trace != NIL) {
                father = trace;
                if (trace.val >= val) {
                    trace = trace.left;
                } else {
                    trace = trace.right;
                }
            }

            Node newNode = new Node();
            newNode.val = val;
            newNode.left = NIL;
            newNode.right = NIL;
            newNode.rank = 1;
            if (father.val >= val) {
                father.setLeft(newNode);
            } else {
                father.setRight(newNode);
            }
            splay(newNode);
        }

        public void splay(Node x) {
            if (x == NIL) {
                root = NIL;
                return;
            }

            Node y, z;
            while ((y = x.father) != NIL) {
                if ((z = y.father) != NIL) {
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
                } else {
                    if (x == y.left) {
                        zig(x);
                    } else {
                        zag(x);
                    }
                }
            }

            root = x;
            x.updateRank();
        }

        public void zig(Node x) {
            Node y = x.father;
            Node p = y.father;
            Node b = x.right;

            x.setRight(y);
            y.setLeft(b);
            p.setSon(y, x);

            y.updateRank();
        }

        public void zag(Node x) {
            Node y = x.father;
            Node p = y.father;
            Node b = x.left;

            x.setLeft(y);
            y.setRight(b);
            p.setSon(y, x);

            y.updateRank();
        }

        @Override
        public String toString() {
            return dfs(root);
        }

        public String dfs(Node node) {
            return node == NIL ? "" : dfs(node.left) + node.val + "," + dfs(node.right);
        }

        private static class Node {
            int val;
            int rank;
            private Node father;
            private Node left;
            private Node right;

            public void setLeft(Node left) {
                this.left = left;
                left.father = this;
            }


            public void setRight(Node right) {
                this.right = right;
                right.father = this;
            }


            public void setSon(Node oldOne, Node newOne) {
                if (left == oldOne) {
                    left = newOne;
                } else {
                    right = newOne;
                }
                newOne.father = this;
            }

            public void updateRank() {
                rank = left.rank + right.rank + 1;
            }

            @Override
            public String toString() {
                return val + ":" + rank;
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
