package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by dalt on 2017/12/13.
 */
public class LongLongMessage2 {
    static BlockReader input;
    String textOne;
    String textTwo;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\LongLongMessage.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            LongLongMessage2 goingHome = new LongLongMessage2();
            goingHome.init();
            System.out.println(goingHome.solve());
        }
    }

    public void init() {
        textOne = input.nextBlock();
        textTwo = input.nextBlock();
    }

    public int solve() {
        char[] data = new StringBuilder(textOne.length() + textTwo.length() + 1)
                .append(textOne).append((char) ('z' + 1)).append(textTwo).append((char) ('z' + 2)).toString().toCharArray();

        int sepPos = textOne.length();

        //Suffix array
        SuffixTree tree = new SuffixTree.SuffixTreeBuilder(data, 'a', 'z' + 3).buildTree();

        SuffixTree.Node[] nodes = tree.getNodeList();
        boolean[] hasLater = new boolean[nodes.length];
        boolean[] hasFormer = new boolean[nodes.length];
        int[] preLength = new int[nodes.length];
        Arrays.fill(preLength, -1);
        preLength[0] = 0;
        for (SuffixTree.Node node : nodes) {
            memoSearch(node, preLength);
            if (node.begin <= sepPos && node.end > sepPos) {
                for (SuffixTree.Node trace = node; !hasFormer[trace.id]; trace = trace.father) {
                    hasFormer[trace.id] = true;
                }
            } else if (node.begin > sepPos && node.end == data.length) {
                for (SuffixTree.Node trace = node; !hasLater[trace.id] && !(node.begin <= sepPos && node.end > sepPos); trace = trace.father) {
                    hasLater[trace.id] = true;
                }
            }
        }

        int max = 0;
        SuffixTree.Node intersectedAt = null;
        for (SuffixTree.Node node : nodes) {
            if (hasFormer[node.id] && hasLater[node.id]) {
                max = Math.max(max, preLength[node.id]);
                if (max == preLength[node.id]) {
                    intersectedAt = node;
                }
            }
        }

        String s = "";
        while (intersectedAt.father != intersectedAt) {
            s = intersectedAt + s;
            intersectedAt = intersectedAt.father;
        }
        System.out.println(s);

        return max;
    }

    public int memoSearch(SuffixTree.Node node, int[] memo) {
        if (memo[node.id] == -1) {
            memo[node.id] = memoSearch(node.father, memo) + node.end - node.begin;
        }
        return memo[node.id];
    }

    public static class SuffixTree {
        Node root;
        List<Node> nodeList;
        int offset;

        private SuffixTree() {
        }

        public Node[] getNodeList() {
            return nodeList.toArray(new Node[nodeList.size()]);
        }

        public static class SuffixTreeBuilder {
            List<Node> nodeList;

            char[] s;
            Node root;
            Node active;
            int ul, ur;
            int offset;
            int range;
            Node father;
            int fatherPrefixLength;
            int p;
            int prefixLength;

            public SuffixTreeBuilder(char[] s, int rangeFrom, int rangeTo) {
                this.s = s;
                offset = rangeFrom;
                range = rangeTo - rangeFrom;
            }

            public SuffixTree buildTree() {
                root = new Node();
                root.children = new Node[range];
                root.father = root;
                root.link = root;
                active = root;
                ul = 0;
                ur = 0;
                nodeList = new ArrayList();
                addIntoList(root);
                root.s = s;
                for (int i = 0, bound = s.length; i < bound; i++) {
                    pro();
                }
                SuffixTree tree = new SuffixTree();
                tree.root = root;
                tree.offset = offset;
                tree.nodeList = nodeList;
                return tree;
            }

            public void pro() {
                ur++;
                father = null;
                judge();
            }

            public void judge() {
                if (ur == ul) {
                    return;
                }


                int activeLength = active.end - active.begin;
                int matchPart = ur - 1 - ul - prefixLength;
                if (activeLength <= matchPart) {
                    p = activeLength;
                    case1();
                } else {
                    p = matchPart;
                    case2();
                }
            }

            public void case1() {
                if (active.children[s[ul + prefixLength + p] - offset] == null) {
                    case3();
                } else {
                    case4();
                }
            }

            public void case2() {
                if (s[active.begin + p] == s[ur - 1]) {
                    case5();
                } else {
                    case6();
                }
            }

            public void case3() {
                Node empty = new Node();
                empty.children = new Node[range];
                empty.begin = ul + prefixLength + p;
                empty.end = s.length;
                empty.link = root;
                empty.s = s;
                empty.father = active;
                active.children[s[empty.begin] - offset] = empty;
                father = active;
                fatherPrefixLength = prefixLength;
                prefixLength = Math.max(prefixLength - active.father.end + active.father.begin - 1, 0);
                active = active.father.link;
                ul++;

                addIntoList(empty);

                judge();
            }

            public void case4() {
                int width = active.end - active.begin;
                active = active.children[s[ul + prefixLength + p] - offset];
                prefixLength += width;

                if (father != null && prefixLength + 1 == fatherPrefixLength) {
                    father.link = active;
                }
                judge();
            }

            public void case5() {
                return;
            }

            public void case6() {
                Node pre = new Node();
                pre.s = s;
                pre.father = active.father;
                active.father = pre;
                pre.children = new Node[range];
                pre.begin = active.begin;
                active.begin += p;
                pre.end = active.begin;
                pre.father.children[s[pre.begin] - offset] = pre;
                pre.children[s[active.begin] - offset] = active;
                pre.link = active.link;
                active.link = root;
                active = pre;
                addIntoList(pre);

                judge();
            }

            public void addIntoList(Node node) {
                node.id = nodeList.size();
                nodeList.add(node);
            }
        }

        public static class Node {
            Node[] children;
            int begin, end;
            Node link;
            Node father;
            int id;

            char[] s;

            @Override
            public String toString() {
                return new String(s, begin, end - begin);
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
