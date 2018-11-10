package com.daltao.oj.old.submit.leetcode;

/**
 * Created by Administrator on 2017/10/15.
 */
public class Trie {
    ArrayPrefixTree tree;

    /**
     * Initialize your data structure here.
     */
    public Trie() {
        tree = new ArrayPrefixTree(new CharacterCompressor() {
            @Override
            public int lowerBound() {
                return 'a';
            }

            @Override
            public int upperBound() {
                return 'z';
            }

            @Override
            public int transform(char c) {
                return c;
            }

            @Override
            public char transfromBack(int c) {
                return (char) c;
            }
        });
    }

    /**
     * Inserts a word into the trie.
     */
    public void insert(String word) {
        tree.add(word);
    }

    /**
     * Returns if the word is in the trie.
     */
    public boolean search(String word) {
        return tree.search(word);
    }

    /**
     * Returns if there is any word in the trie that starts with the given prefix.
     */
    public boolean startsWith(String prefix) {
        return tree.startWith(prefix);
    }

    public interface CharacterCompressor {
        public int lowerBound();

        public int upperBound();

        public int transform(char c);

        public char transfromBack(int c);
    }

    public static class ArrayPrefixTree {
        private CharacterCompressor characterCompressor;
        private int charSetSize;
        private int lowerBound;
        private int upperBound;
        private Node root;

        private ArrayPrefixTree(CharacterCompressor characterCompressor) {
            this.characterCompressor = characterCompressor;
            this.charSetSize = characterCompressor.upperBound() - characterCompressor.lowerBound() + 1;
            root = new Node(charSetSize);
            this.lowerBound = characterCompressor.lowerBound();
        }

        public static ArrayPrefixTree getInstance(CharacterCompressor characterCompressor) {
            return new ArrayPrefixTree(characterCompressor);
        }

        private int transform(char c) {
            return characterCompressor.transform(c) - lowerBound;
        }

        private char transformBack(int index) {
            return characterCompressor.transfromBack(index + lowerBound);
        }

        public void add(CharSequence charSequence) {
            Node iter = root;
            for (int i = 0, bound = charSequence.length(); i < bound; i++) {
                char c = charSequence.charAt(i);
                int index = transform(c);
                iter = iter.getOrAddSubNode(index);
            }

            iter.setComplete();
        }

        public boolean search(CharSequence charSequence) {
            Node iter = root;
            for (int i = 0, bound = charSequence.length(); i < bound && iter != null; i++) {
                char c = charSequence.charAt(i);
                int index = transform(c);
                iter = iter.getSubNode(index);
            }
            return iter != null && iter.isComplete();
        }

        public CharSequence anyStartWith(CharSequence charSequence) {
            Node iter = getNodeStartWith(charSequence);
            if (iter == null) {
                return null;
            }


            StringBuilder builder = new StringBuilder(charSequence);
            while (!iter.isComplete()) {
                int index = iter.getAnySubIndex();
                builder.append(transformBack(index));
                iter = iter.getSubNode(index);
            }
            return builder.toString();
        }

        private Node getNodeStartWith(CharSequence charSequence) {
            Node iter = root;
            for (int i = 0, bound = charSequence.length(); i < bound && iter != null; i++) {
                char c = charSequence.charAt(i);
                int index = transform(c);
                iter = iter.getSubNode(index);
            }
            return iter;
        }

        public boolean startWith(CharSequence charSequence) {
            return getNodeStartWith(charSequence) != null;
        }


        private static class Node {
            boolean completeFlag;
            Node[] subNodes;
            int index = -1;
            byte flag;

            public Node(int size) {
                subNodes = new Node[size];
            }

            public int getAnySubIndex() {
                return index;
            }

            public Node getOrAddSubNode(int index) {
                if (subNodes[index] == null) {
                    subNodes[index] = new Node(subNodes.length);
                    this.index = index;
                }
                return subNodes[index];
            }

            public void removeSubNode(int index) {
                if (subNodes[index] != null) {
                    subNodes[index] = null;
                }
            }

            public Node getSubNode(int index) {
                return subNodes[index];
            }

            public boolean isComplete() {
                return completeFlag;
            }

            public void setComplete() {
                completeFlag = true;
            }

            public void setNotComplete() {
                completeFlag = false;
            }
        }
    }

}
