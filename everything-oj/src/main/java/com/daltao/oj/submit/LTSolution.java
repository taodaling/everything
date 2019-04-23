package com.daltao.oj.submit;

import java.util.*;

public class LTSolution {

    public static void main(String[] args) {

        Solution streamChecker = new Solution(new String[]{"cd", "f", "kl"}); // init the dictionary.
        streamChecker.query('a');          // return false
        streamChecker.query('b');          // return false
        streamChecker.query('c');          // return false
        streamChecker.query('d');          // return true, because 'cd' is in the wordlist
        streamChecker.query('e');          // return false
        streamChecker.query('f');          // return true, because 'f' is in the wordlist
        streamChecker.query('g');          // return false
        streamChecker.query('h');          // return false
        streamChecker.query('i');          // return false
        streamChecker.query('j');          // return false
        streamChecker.query('k');          // return false
        streamChecker.query('l');          // return true, because 'kl' is in the wordlist
    }


    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    static class Solution {
        ACAutomaton acm = new ACAutomaton();

        public Solution(String[] words) {
            for (String s : words) {
                acm.beginBuild();
                for (int i = 0, until = s.length(); i < until; i++) {
                    acm.build(s.charAt(i));
                }
                acm.buildLast.word = true;
            }
            acm.endBuild();
            acm.beginMatch();
        }

        public boolean query(char letter) {
            acm.match(letter);
            return acm.matchLast.word;
        }

        public static class ACAutomaton {
            static final int MIN_CHARACTER = 'a';
            static final int MAX_CHARACTER = 'z';
            static final int RANGE_SIZE = MAX_CHARACTER - MIN_CHARACTER + 1;
            int size;
            Node root;
            Node buildLast;
            Node matchLast;

            public ACAutomaton() {
                root = new Node();
                size = 1;
            }

            public void beginBuild() {
                buildLast = root;
            }

            public void endBuild() {
                Deque<Node> deque = new ArrayDeque<>(size);

                for (Node next : root.next) {
                    if (next == null) {
                        continue;
                    }
                    deque.addLast(next);
                }

                while (!deque.isEmpty()) {
                    Node head = deque.removeFirst();
                    Node fail = visit(head.father.fail, head.index);
                    if (fail == null) {
                        head.fail = root;
                    } else {
                        head.fail = fail.next[head.index];
                    }
                    head.word = head.word || head.fail.word;

                    for (Node next : head.next) {
                        if (next == null) {
                            continue;
                        }
                        deque.addLast(next);
                    }
                }
            }

            public Node visit(Node trace, int index) {
                while (trace != null && trace.next[index] == null) {
                    trace = trace.fail;
                }
                return trace;
            }

            public void build(char c) {
                int index = c - MIN_CHARACTER;
                if (buildLast.next[index] == null) {
                    Node node = new Node();
                    node.father = buildLast;
                    node.index = index;
                    buildLast.next[index] = node;
                    size++;
                }
                buildLast = buildLast.next[index];
            }

            public void beginMatch() {
                matchLast = root;
            }

            public void match(char c) {
                int index = c - MIN_CHARACTER;
                Node fail = visit(matchLast, index);
                if (fail == null) {
                    matchLast = root;
                } else {
                    matchLast = fail.next[index];
                }
            }

            public static class Node {
                Node[] next = new Node[RANGE_SIZE];
                Node fail;
                Node father;
                int index;
                boolean word;

                @Override
                public String toString() {
                    return father == null ? "" : (father.toString() + (char) (MIN_CHARACTER + index));
                }
            }
        }
    }
}
