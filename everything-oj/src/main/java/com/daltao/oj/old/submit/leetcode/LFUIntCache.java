package com.daltao.oj.old.submit.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/25.
 */
public class LFUIntCache {
    private LFUCache0<Integer, Integer> cache;
    public LFUIntCache(int capacity) {
        cache = new LFUCache0<Integer, Integer>(capacity);
    }
    public int get(int key) {
        Integer res = cache.get(key);
        return res == null ? -1 : res.intValue();
    }
    public void put(int key, int value) {
        cache.put(key, value);
    }
    public static class LFUCache0<K, V> {
        int order;
        int capacity;
        Strategy<K, V> current;
        Map<K, LFUNode<K, V>> map = new HashMap<K, LFUNode<K, V>>();
        UncheckedLinkedList<LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>> lists =
                new UncheckedLinkedList<LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>>();
        UncheckedLinkedList<LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>> reuse =
                new UncheckedLinkedList<LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>>();
        public LFUCache0(int capacity) {
            this.capacity = capacity;
            for (int i = 0; i < capacity; i++) {
                LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> wrapper = new LinkedNodeWrapper<>();
                wrapper.val = new UncheckedLinkedList();
                reuse.addAsLast(wrapper);
            }
            if (capacity > 0) {
                current = new PrepareStrategy();
            } else {
                current = new ZeroStrategy();
            }
        }
        public void put(K key, V value) {
            current.put(key, value);
        }
        public V get(K key) {
            return current.get(key);
        }
        private LFUNode<K, V> newNode(K key, V val) {
            LFUNode<K, V> node = new LFUNode<>();
            node.order = order++;
            node.val = val;
            node.key = key;
            return node;
        }
        private LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> leave(LFUNode<K, V> node) {
            LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> next = (LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>) lists.getNext(node.aware);
            node.aware.val.removeFrom(node);
            if (node.aware.val.size() == 0) {
                lists.removeFrom(node.aware);
                reuse.addAsLast(node.aware);
            }
            return next;
        }
        private void appendBeforeOrAtTail(LFUNode<K, V> node,
                                          LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> aware) {
            if (aware == null) {
                aware = reuse.getLast();
                reuse.removeFrom(aware);
                lists.addAsLast(aware);
            }
            if (aware.val.size() == 0) {
                aware.val.addAsLast(node);
            } else if (aware.val.getFirst().visitedTime == node.visitedTime) {
                aware.val.addAsLast(node);
            } else {
                LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> next = aware;
                aware = reuse.getLast();
                reuse.removeFrom(aware);
                lists.addBefore(next, aware);
                aware.val.addAsLast(node);
            }
            node.aware = aware;
        }
        private void updateExisting(LFUNode<K, V> node) {
            appendBeforeOrAtTail(node, leave(node));
        }
        private void invalid(LFUNode<K, V> node) {
            leave(node);
        }
        private interface Strategy<K, V> {
            public V get(K key);
            public void put(K key, V value);
        }
        private static class LFUNode<K, V> extends UncheckedLinkedList.LinkedNode {
            int order;
            int visitedTime;
            K key;
            V val;
            LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> aware;
        }
        private static class LinkedNodeWrapper<T> extends UncheckedLinkedList.LinkedNode {
            T val;
        }
        public static class UncheckedLinkedList<T extends UncheckedLinkedList.LinkedNode> {
            int size;
            LinkedNode guard = new LinkedNode();
            {
                clear();
            }
            public void clear() {
                guard.previous = guard.next = guard;
                size = 0;
            }
            public T getFirst() {
                return guard.next == guard ? null : (T) guard.next;
            }
            public T getNext(T t) {
                return t.next == guard ? null : (T) t.next;
            }
            public T getLast() {
                return guard.previous == guard ? null : (T) guard.previous;
            }
            public UncheckedLinkedList<T> addAsFirst(T t) {
                return addAfter(guard, t);
            }
            public UncheckedLinkedList<T> addAsLast(T t) {
                return addBefore(guard, t);
            }
            public void removeFrom(LinkedNode t) {
                LinkedNode node = (LinkedNode) t;
                node.previous.next = node.next;
                node.next.previous = node.previous;
                size--;
            }
            public UncheckedLinkedList<T> addAfter(LinkedNode former, T t) {
                LinkedNode node = t;
                former.next.previous = t;
                node.next = former.next;
                former.next = node;
                node.previous = former;
                size++;
                return this;
            }
            public UncheckedLinkedList<T> addBefore(LinkedNode later, T t) {
                LinkedNode node = t;
                later.previous.next = t;
                node.previous = later.previous;
                later.previous = node;
                node.next = later;
                size++;
                return this;
            }
            public int size() {
                return size;
            }
            public static class LinkedNode {
                public LinkedNode previous;
                public LinkedNode next;
            }
        }
        private class ZeroStrategy implements Strategy<K, V> {
            @Override
            public V get(K key) {
                return null;
            }
            @Override
            public void put(K key, V value) {
            }
        }
        private class ReadyStrategy extends PrepareStrategy {
            public void put(K key, V value) {
                LFUNode<K, V> node = get0(key);
                if (node != null) {
                    node.val = value;
                    return;
                }
                node = newNode(key, value);
                //Invalidate a node
                LFUNode<K, V> removeObj = lists.getFirst().val.getFirst();
                leave(removeObj);
                map.remove(removeObj.key);
                //Insert new node
                map.put(key, node);
                appendBeforeOrAtTail(node, lists.getFirst());
            }
        }
        private class PrepareStrategy implements Strategy<K, V> {
            @Override
            public V get(K key) {
                LFUNode<K, V> node = get0(key);
                return node == null ? null : node.val;
            }

            protected LFUNode<K, V> get0(K key) {
                LFUNode<K, V> node = map.get(key);
                if (node == null) {
                    return null;
                }
                node.visitedTime++;
                node.order = order++;
                appendBeforeOrAtTail(node, leave(node));
                return node;
            }
            @Override
            public void put(K key, V value) {
                LFUNode<K, V> node = get0(key);
                if (node != null) {
                    node.val = value;
                    return;
                }
                node = newNode(key, value);
                map.put(key, node);
                appendBeforeOrAtTail(node, lists.getFirst());
                if (map.size() == capacity) {
                    current = new ReadyStrategy();
                }
            }
        }
    }
}
