package com.daltao.oj.old.submit.leetcode;

import java.util.*;

/**
 * Created by Administrator on 2017/9/8.
 */
public class WordLadderII {
    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        if (beginWord.equals(endWord)) {
            return Arrays.asList(Arrays.asList(beginWord));
        }

        boolean pass = false;
        for (String word : wordList) {
            if (word.equals(endWord)) {
                pass = true;
                break;
            }
        }
        if (!pass) {
            return Collections.emptyList();
        }

        int n = wordList.size();
        int m = beginWord.length();

        Node[] node = new Node[n + 2];
        for (int i = 0, bound = wordList.size(); i < bound; i++) {
            node[i] = new Node();
            node[i].signature = wordList.get(i);
            node[i].flag = Integer.MAX_VALUE;
        }

        node[n] = new Node();
        node[n].signature = beginWord;
        node[n + 1] = new Node();
        node[n + 1].signature = endWord;
        Node src = node[n];
        Node dst = node[n + 1];
        src.flag = 0;
        dst.flag = Integer.MAX_VALUE;

        //Build graph
        for (int i = 0, bound = n + 2; i < bound; i++) {
            for (int j = i + 1; j < bound; j++) {
                if (differForOneLetter(node[i].signature, node[j].signature)) {
                    node[i].nearTo.add(node[j]);
                    node[j].nearTo.add(node[i]);
                }
            }
        }

        //BFS
        Deque<Node> queue = new LinkedList<>();
        queue.add(src);
        while (!queue.isEmpty()) {
            Node front = queue.pollFirst();
            int nextFlag = front.flag + 1;
            for (Node near : front.nearTo) {
                if (near.flag == nextFlag) {
                    near.parent.add(front);
                } else if (near.flag > nextFlag) {
                    near.flag = nextFlag;
                    near.parent.add(front);
                    queue.addLast(near);
                }
            }
        }

        List<List<String>> result = new LinkedList<>();
        if (dst.flag == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }
        dfs(new String[dst.flag + 1], dst.flag, dst, result);
        return result;
    }

    public void dfs(String[] route, int index, Node current, List<List<String>> routes) {
        route[index] = current.signature;
        if (index == 0) {
            routes.add(Arrays.asList(route.clone()));
            return;
        }
        for (Node parent : current.parent) {
            dfs(route, index - 1, parent, routes);
        }
    }

    public boolean differForOneLetter(String a, String b) {
        int differNum = 0;
        for (int i = 0, bound = a.length(); i < bound; i++) {
            differNum += a.charAt(i) == b.charAt(i) ? 0 : 1;
        }
        return differNum == 1;
    }

    private static class Node {
        String signature;
        int flag;
        List<Node> parent = new LinkedList<>();
        List<Node> nearTo = new LinkedList<>();

        @Override
        public String toString() {
            return signature + ":" + flag;
        }
    }
}
