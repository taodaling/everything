package com.daltao.template;

import java.util.Comparator;

public class SLNode<K, V> {
    private SLNode<K, V> bottom;
    private SLNode<K, V> right;
    private SLNode<K, V> left;
    private K key;
    private V val;

    public static <K, V> SLNode<K, V> build(int height) {
        if (height == 0) {
            return null;
        }

        SLNode node = new SLNode();
        node.key = null;
        node.bottom = build(height - 1);
        return node;
    }

    public static <K, V> V get(SLNode<K, V> root, K key, Comparator<K> cmp) {
        if (root == null) {
            return null;
        }

        int compResult = root.key == null ? -1 : cmp.compare(root.key, key);
        if (compResult > 0) {
            return null;
        } else if (compResult == 0) {
            return root.val;
        } else {
            if (root.right != null && cmp.compare(root.right.key, key) <= 0) {
                return get(root.right, key, cmp);
            }
            return get(root.bottom, key, cmp);
        }
    }

    public static <K, V> boolean insert(SLNode<K, V> root, K key, Comparator<K> cmp) {
        return false;
    }
}