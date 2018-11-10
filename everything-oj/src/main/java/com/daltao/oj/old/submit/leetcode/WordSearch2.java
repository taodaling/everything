package com.daltao.oj.old.submit.leetcode;

import java.util.*;

/**
 * Created by dalt on 2018/5/1.
 */
public class WordSearch2 {
    public static void main(String[] args)
    {
        System.out.println(new WordSearch2().findWords(
                new char[][]{{'o','a','a','n'},{'e','t','a','e'},{'i','h','k','r'},{'i','f','l','v'}},
                new String[]{"oath","pea","eat","rain"}
        ));
    }

    public List<String> findWords(char[][] board, String[] words) {
        if(board.length == 0 || board[0].length == 0)
        {
            return Collections.emptyList();
        }

        PrefixTree tree = new PrefixTree();
        for(String s : words)
        {
            tree.beginBuild();
            for(int i = 0, until = s.length(); i < until; i++)
            {
                tree.build(s.charAt(i));
            }
            tree.buildLast.s = s;
        }


        int n = board.length;
        int m = board[0].length;
        List<String> result = new ArrayList();
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < m; j++)
            {
                tree.beginMatch();
                search(tree, board, i, j, result);
            }
        }

        return result;
    }

    public static void search(PrefixTree tree, char[][] board, int r, int c, List<String> result)
    {
        if(r < 0 || r >= board.length || c < 0 || c >= board[0].length)
        {
            return;
        }
        if(board[r][c] >= 128)
        {
            return;
        }

        tree.push();

        tree.match(board[r][c]);


        int state = tree.state();
        if(state != 0)
        {
            if(state == 2)
            {
                result.add(tree.matchLast.s);
            }

            board[r][c] += 128;
            search(tree, board, r + 1, c, result);
            search(tree, board, r - 1, c, result);
            search(tree, board, r, c + 1, result);
            search(tree, board, r, c - 1, result);
            board[r][c] -= 128;
        }

        tree.pop();
    }

    public static class Node{
        Node[] next = new Node[26];
        String s;
    }

    public static class PrefixTree{
        Node root = new Node();
        Node buildLast;
        Node matchLast;

        Deque<Node> deque = new ArrayDeque();
        public void beginBuild(){
            buildLast = root;
        }
        public void build(char c)
        {
            int i = c - 'a';
            if(buildLast.next[i] == null)
            {
                Node next = new Node();
                buildLast.next[i] = next;
            }
            buildLast = buildLast.next[i];
        }

        public void beginMatch(){
            matchLast = root;
        }
        public void match(char c){
            int i = c - 'a';
            matchLast = matchLast.next[i];
        }
        public void pop(){
            matchLast = deque.removeLast();
        }
        public void push(){
            deque.addLast(matchLast);
        }
        public int state(){
            return matchLast == null ? 0 : matchLast.s != null ? 2 : 1;
        }
    }
}
