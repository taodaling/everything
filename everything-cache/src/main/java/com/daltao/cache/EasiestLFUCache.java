package com.daltao.cache;

import com.daltao.utils.Precondition;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * A simple LFU implementation, all methods provided by this class have O(log2n) time complexity.
 */
public class EasiestLFUCache<K, V> implements Cache<K, V> {
    public EasiestLFUCache(int limitation) {
        Precondition.ge(limitation, 1);
        this.limitation = limitation;
        existence = new HashMap<>(limitation);
    }

    private static class Node<K, V> {
        private int time;
        private K key;
        private V value;
        private int frequency;

        @Override
        public String toString() {
            return key + ":" + value + ":" + frequency + ":" + time;
        }
    }

    private Map<K, Node<K, V>> existence;
    private TreeSet<Node<K, V>> orderedSet = new TreeSet<>((a, b) -> {
        if (a.frequency != b.frequency) {
            return a.frequency - b.frequency;
        }
        return a.time - b.time;
    });

    private int sequence = 0;
    private int limitation;

    private void update(Node node) {
        orderedSet.remove(node);
        node.frequency++;
        node.time = sequence++;
        orderedSet.add(node);
    }

    @Override
    public boolean contain(K key) {
        Node<K, V> node = existence.get(key);
        if (node != null) {
            update(node);
            return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = existence.get(key);
        if (node != null) {
            update(node);
            return node.value;
        }
        return null;
    }

    @Override
    public void add(K key, V value) {
        Node<K, V> node = existence.get(key);
        if (node != null) {
            node.value = value;
            update(node);
            return;
        }
        if (existence.size() == limitation) {
            Node eldest = orderedSet.first();
            orderedSet.remove(eldest);
            existence.remove(eldest.key);
        }

        node = new Node();
        node.key = key;
        node.value = value;
        existence.put(key, node);
        update(node);
    }

    @Override
    public void purge(K key) {
        Node node = existence.remove(key);
        if (node != null) {
            orderedSet.remove(node);
        }
    }

    @Override
    public void purgeAll() {
        existence.clear();
        orderedSet.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (Node<K, V> node : orderedSet) {
            builder.append(node.key).append(":").append(node.value).append(":").append(node.frequency).append(", ");
        }
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        }
        builder.append("}");
        return builder.toString();
    }
}
