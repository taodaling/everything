package com.daltao.oj.topcoder;

public class AWordGame {
    public String outcome(String[] wordList) {
        Trie root = new Trie();
        for (String w : wordList) {
            for (String part : w.split(" ")) {
                addWord(root, part, 0);
            }
        }

        dfs(root);
        Trie trace = root.choice;
        while (trace.choice != null) {
            trace = trace.choice;
        }

        return trace.s;
    }

    public void dfs(Trie root) {
        if (root.word) {
            root.win = true;
            root.longest = 0;
            return;
        }

        for (int i = 0; i < root.next.length; i++) {
            if (root.next[i] == null) {
                continue;
            }
            dfs(root.next[i]);
            root.win = root.win || !root.next[i].win;
        }

        if (root.win) {
            root.longest = Integer.MAX_VALUE;
            for (int i = 0; i < root.next.length; i++) {
                if (root.next[i] == null || root.next[i].win) {
                    continue;
                }
                if (root.longest > root.next[i].longest + 1) {
                    root.longest = root.next[i].longest + 1;
                    root.choice = root.next[i];
                }
            }
        } else {
            for (int i = 0; i < root.next.length; i++) {
                if (root.next[i] == null) {
                    continue;
                }
                if (root.longest < root.next[i].longest + 1) {
                    root.longest = root.next[i].longest + 1;
                    root.choice = root.next[i];
                }
            }
        }
    }

    public void addWord(Trie root, String s, int i) {
        if (i == s.length()) {
            root.word = true;
            root.s = s;
            return;
        }
        addWord(root.get(s.charAt(i) - 'a'), s, i + 1);
    }

    public static class Trie {
        Trie[] next = new Trie['z' - 'a' + 1];
        boolean win;
        int longest;
        boolean word;
        Trie choice;
        int c = -1;
        Trie p;
        String s;

        @Override
        public String toString() {
            return c == -1 ? "" : p.toString() + (char) (c + 'a');
        }

        Trie get(int i) {
            if (next[i] == null) {
                next[i] = new Trie();
                next[i].c = i;
                next[i].p = this;
            }
            return next[i];
        }
    }
}
