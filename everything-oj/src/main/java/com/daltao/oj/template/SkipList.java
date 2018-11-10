package com.daltao.oj.template;

import java.util.*;

public class SkipList<K, V> extends AbstractMap<K, V> {
    Set<Entry<K, V>> set = new AbstractSet<Entry<K, V>>() {
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                Node<K, V> trace = minNode;

                @Override
                public boolean hasNext() {
                    return trace.next[0] != maxNode;
                }

                @Override
                public Entry<K, V> next() {
                    return (trace = trace.next[0]);
                }
            };
        }

        @Override
        public int size() {
            return size;
        }
    };


    @Override
    public boolean remove(Object key, Object value) {
        Node<K, V> node = search0(minNode, treeHeight - 1, (K) key);
        if (comp.compare((K) key, node.key) != 0) {
            return false;
        }
        if (!Objects.equals(value, node.value)) {
            return false;
        }

        removeNode(node);

        return true;
    }

    @Override
    public V remove(Object key) {
        Node<K, V> node = search0(minNode, treeHeight - 1, (K) key);
        if (comp.compare((K) key, node.key) != 0) {
            return null;
        }
        removeNode(node);
        return node.value;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return set;
    }

    public static class Node<K, V> implements Entry<K, V> {
        Node<K, V>[] next;
        Node<K, V>[] prev;
        K key;
        V value;

        public Node(K key, V value, int h) {
            this.key = key;
            this.value = value;
            next = new Node[h];
            prev = new Node[h];
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    Node<K, V> minNode;
    Node<K, V> maxNode;
    int treeHeight;
    Comparator<K> comp;
    Random random = new Random();
    int size;

    public SkipList() {
        this((a, b) -> ((Comparable<K>) a).compareTo(b));
    }

    public SkipList(final Comparator<K> comparator) {
        treeHeight = 0;

        Object min = new Object();
        Object max = new Object();

        minNode = new Node<K, V>((K) min, null, 1);
        maxNode = new Node<K, V>((K) max, null, 1);
        this.comp = new Comparator<K>() {
            @Override
            public int compare(K o1, K o2) {
                if (o1 == min || o2 == min) {
                    if (o1 == min) {
                        return o2 == min ? 0 : -1;
                    } else {
                        return 1;
                    }
                } else if (o1 == max || o2 == max) {
                    if (o1 == max) {
                        return o2 == max ? 0 : 1;
                    } else {
                        return -1;
                    }
                }
                return comparator.compare(o1, o2);
            }
        };

        incrementHeight();
    }

    public V put(K key, V value) {
        Node<K, V> node = put0(minNode, treeHeight - 1, key, value);
        V old = node.value;
        node.value = value;

        while (treeHeight < node.next.length) {
            incrementHeight();
            insertAfter(minNode, node, treeHeight - 1);
        }

        return old;
    }

    public Entry<K, V> ceilingEntry(K key, boolean include) {
        Node<K, V> node = search0(minNode, treeHeight - 1, key);
        int compRes = comp.compare(key, node.key);
        if (!(compRes == 0 && include)) {
            node = node.next[0];
        }
        return exceedCheck(node);
    }

    public Entry<K, V> floorEntry(K key, boolean include) {
        Node<K, V> node = search0(minNode, treeHeight - 1, key);
        int compRes = comp.compare(key, node.key);
        if (compRes == 0 && !include) {
            node = node.prev[0];
        }
        return exceedCheck(node);
    }

    public Entry<K, V> firstEntry() {
        return exceedCheck(minNode.next[0]);
    }

    public Entry<K, V> lastEntry() {
        return exceedCheck(maxNode.prev[0]);
    }

    private Entry<K, V> exceedCheck(Node<K, V> node) {
        return node == minNode || node == maxNode ? null : node;
    }

    private Node<K, V> put0(Node<K, V> from, int h, K key, V value) {
        from = findLesserOrEqual(from, h, key);
        if (comp.compare(from.key, key) == 0) {
            from.value = value;
            return from;
        } else if (h > 0) {
            Node<K, V> node = put0(from, h - 1, key, value);
            if (node.next.length > h) {
                insertAfter(from, node, h);
            }
            return node;
        } else {
            Node<K, V> node = new Node<>(key, null, getRandomHeight() + 1);
            insertAfter(from, node, h);
            size++;
            return node;
        }
    }

    private int getRandomHeight() {
        int x = 0;
        while (random.nextBoolean()) {
            x++;
        }
        return x;
    }

    private void incrementHeight() {

        if (minNode.prev.length <= treeHeight) {
            minNode.prev = expand(minNode.prev);
            minNode.next = expand(minNode.next);

            maxNode.prev = expand(maxNode.prev);
            maxNode.next = expand(maxNode.next);
        }
        minNode.next[treeHeight] = maxNode;
        maxNode.prev[treeHeight] = minNode;

        treeHeight++;
    }

    private static <K, V> Node<K, V>[] expand(Node<K, V>[] data) {
        return Arrays.copyOf(data, data.length << 1);
    }

    private static <K, V> void insertAfter(Node<K, V> former, Node<K, V> newNode, int h) {
        Node<K, V> later = former.next[h];

        former.next[h] = newNode;
        newNode.prev[h] = former;
        later.prev[h] = newNode;
        newNode.next[h] = later;
    }

    private <K, V> void removeNode(Node<K, V> node) {
        int h = node.next.length;

        for (int i = 0; i < h; i++) {
            Node prev = node.prev[i];
            Node next = node.next[i];

            prev.next[i] = next;
            next.prev[i] = prev;
        }

        size--;
    }

    @Override
    public void clear() {
        treeHeight = 0;
        incrementHeight();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Node<K, V> node = search0(minNode, treeHeight - 1, (K) key);
        if (comp.compare((K) key, node.key) == 0) {
            return node.value;
        } else {
            return defaultValue;
        }
    }

    public V get(Object key) {
        Node<K, V> node = search0(minNode, treeHeight - 1, (K) key);
        if (comp.compare(node.key, (K) key) != 0) {
            return null;
        }
        return node.value;
    }

    @Override
    public boolean containsKey(Object key) {
        return comp.compare(search0(minNode, treeHeight - 1, (K) key).key, (K) key) == 0;
    }

    /**
     * Find the greatest element E that behind (exclusive) from satisfy E.key<=key
     */
    private Node<K, V> findLesserOrEqual(Node<K, V> from, int h, K key) {
        from = from.next[h];
        while (comp.compare(from.key, key) <= 0) {
            from = from.next[h];
        }
        return from.prev[h];
    }

    private Node<K, V> search0(Node<K, V> from, int h, K key) {
        from = findLesserOrEqual(from, h, key);
        if (comp.compare(from.key, key) == 0) {
            return from;
        } else if (h == 0) {
            return from;
        } else {
            return search0(from, h - 1, key);
        }
    }
}