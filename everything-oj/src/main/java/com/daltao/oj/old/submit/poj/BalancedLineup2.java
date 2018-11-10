package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/23.
 */
public class BalancedLineup2 {
    static BlockReader input;
    int[] heights;
    List<Question>[] minQuestionLists;
    List<Question>[] maxQuestionLists;
    Question[] minQuestions;
    Question[] maxQuestions;
    int cowNum;
    int questionNum;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\BalancedLineup.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            BalancedLineup2 solution = new BalancedLineup2();
            solution.init();
            System.out.print(solution.solve());
        }
    }

    public void init() {
        cowNum = input.nextInteger();
        questionNum = input.nextInteger();
        heights = new int[cowNum + 1];
        minQuestionLists = new ArrayList[cowNum + 1];
        minQuestionLists[0] = new ArrayList();
        maxQuestionLists = new ArrayList[cowNum + 1];
        maxQuestionLists[0] = new ArrayList();
        minQuestions = new Question[questionNum];
        maxQuestions = new Question[questionNum];
        for (int i = 1; i <= cowNum; i++) {
            heights[i] = input.nextInteger();
            minQuestionLists[i] = new ArrayList();
            maxQuestionLists[i] = new ArrayList();
        }

        for (int i = 0; i < questionNum; i++) {
            int from = input.nextInteger();
            int to = input.nextInteger();

            Question minQuestion = new Question();
            minQuestion.from = from;
            minQuestion.to = to;
            minQuestionLists[from].add(minQuestion);
            minQuestionLists[to].add(minQuestion);
            minQuestions[i] = minQuestion;

            Question maxQuestion = new Question();
            maxQuestion.from = from;
            maxQuestion.to = to;
            maxQuestionLists[from].add(maxQuestion);
            maxQuestionLists[to].add(maxQuestion);
            maxQuestions[i] = maxQuestion;
        }
    }

    public String solve() {
        StringBuilder builder = new StringBuilder();
        Tree minTree = buildTree(heights, new IntComparator() {
            public int compare(int a, int b) {
                return a - b;
            }
        });
        Tree maxTree = buildTree(heights, new IntComparator() {
            public int compare(int a, int b) {
                return b - a;
            }
        });

        lca(minTree.root, minTree.nodes, minQuestionLists);
        lca(maxTree.root, maxTree.nodes, maxQuestionLists);

        for (int i = 0; i < questionNum; i++) {
            builder.append(maxQuestions[i].answer - minQuestions[i].answer).append('\n');
        }
        return builder.toString();
    }

    public void lca(Node root, Node[] nodes, List<Question>[] questionLists) {
        root.ancestor = root;
        if (root.left != null) {
            lca(root.left, nodes, questionLists);
            Node.union(root.left, root).ancestor = root;
        }
        if (root.right != null) {
            lca(root.right, nodes, questionLists);
            Node.union(root.right, root).ancestor = root;
        }
        root.visited = true;
        for (Question question : questionLists[root.index]) {
            Node other = root.index == question.from ? nodes[question.to] : nodes[question.from];
            if (other.visited) {
                question.answer = other.getRepr().ancestor.value;
            }
        }
    }

    public Tree buildTree(int[] values, IntComparator cmp) {
        Node[] nodes = new Node[values.length];
        LinkedList<Node> stack = new LinkedList();
        Node dummy = new Node();
        stack.addLast(dummy);
        for (int i = 0, bound = values.length; i < bound; i++) {
            Node node = new Node();
            node.value = values[i];
            node.index = i;
            nodes[i] = node;
            while (stack.size() > 1 && cmp.compare(stack.getLast().value, values[i]) > 0) {
                stack.removeLast();
            }
            Node father = stack.getLast();
            node.left = father.right;
            father.right = node;
            stack.addLast(node);
        }
        Tree tree = new Tree();
        tree.nodes = nodes;
        tree.root = dummy.right;
        return tree;
    }

    public static interface IntComparator {
        int compare(int a, int b);
    }

    public static class Question {
        int from;
        int to;
        int answer;
    }

    public static class Tree {
        Node root;
        Node[] nodes;
    }

    public static class Node {
        Node left;
        Node right;
        int value;
        int index;
        boolean visited;

        Node ancestor;

        Node p = this;
        int rank;

        public static Node union(Node a, Node b) {
            a = a.getRepr();
            b = b.getRepr();
            if (a == b) {
                return a;
            }
            if (a.rank == b.rank) {
                a.rank++;
            }
            if (a.rank > b.rank) {
                b.p = a;
                return a;
            } else {
                a.p = b;
                return b;
            }
        }

        public Node getRepr() {
            if (p != p.p) {
                p = p.getRepr();
            }
            return p;
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
