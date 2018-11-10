package com.daltao.oj.old.submit.leetcode;

import java.util.*;

/**
 * Created by dalt on 2017/9/25.
 */
public class LFUCache<K, V> {
    int capacity;
    Strategy<K, V> current = new PrepareStrategy();

    Map<K, LFUNode<K, V>> map = new HashMap<K, LFUNode<K, V>>(capacity);
    UncheckedLinkedList<LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>> lists =
            new UncheckedLinkedList<LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>>();
    UncheckedLinkedList<LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>> reuse =
            new UncheckedLinkedList<LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>>();


    public LFUCache(int capacity) {
        this.capacity = capacity;
        for (int i = 0; i < capacity; i++) {
            LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> wrapper = new LinkedNodeWrapper<>();
            wrapper.val = new UncheckedLinkedList<>();
            reuse.add(wrapper);
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
        node.val = val;
        node.key = key;
        return node;
    }

    private LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> leave(LFUNode<K, V> node) {
        LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> next = (LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>>) lists.getNext(node.aware);
        node.aware.val.remove(node);
        if (node.aware.val.size() == 0) {
            lists.remove(node.aware);
            reuse.add(node.aware);
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

    @Override
    public String toString() {
        return map.toString();
    }

    private interface Strategy<K, V> {
        public V get(K key);

        public void put(K key, V value);
    }

    private static class LFUNode<K, V> extends UncheckedLinkedList.LinkedNode {
        int visitedTime;
        K key;
        V val;
        LinkedNodeWrapper<UncheckedLinkedList<LFUNode<K, V>>> aware;

        @Override
        public String toString() {
            return val + ":" + visitedTime;
        }
    }

    private static class LinkedNodeWrapper<T> extends UncheckedLinkedList.LinkedNode {
        T val;
    }

    /**
     * This class represent a linked list without any check operation, so its fast.
     * But be careful when you use this class, because some unrecognized operation will after
     * the safe of your programing.
     * <br>
     * This structure support insert, remove, iterate in O(1) time complexity.
     * <br>
     * Created by dalt on 2017/8/17.
     */
    public static class UncheckedLinkedList<T extends UncheckedLinkedList.LinkedNode> extends AbstractList<T> {
        int size;
        LinkedNode guard = new LinkedNode();

        {
            clear();
        }

        public T getFirst() {
            return guard.next == guard ? null : (T) guard.next;
        }

        public T getNext(T t) {
            return t.next == guard ? null : (T) t.next;
        }

        public T getPrevious(T t) {
            return t.previous == guard ? null : (T) t.previous;
        }

        public T getLast() {
            return guard.previous == guard ? null : (T) guard.previous;
        }

        @Override
        public void clear() {
            guard.previous = guard.next = guard;
            size = 0;
        }

        @Override
        public boolean add(T t) {
            addAsLast(t);
            return true;
        }

        @Override
        public T get(int index) {
            for (T val : this) {
                if (index == 0) {
                    return val;
                }
                index--;
            }
            throw new IndexOutOfBoundsException();
        }

        public UncheckedLinkedList<T> addAsFirst(T t) {
            return addAfter(guard, t);
        }

        public UncheckedLinkedList<T> addAsLast(T t) {
            return addBefore(guard, t);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof LinkedNode)) {
                return false;
            }
            removeFrom((LinkedNode) o);
            return true;
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

        @Override
        public int size() {
            return size;
        }

        @Override
        public ListIterator<T> listIterator() {
            return new ListIterator<T>() {
                LinkedNode cur = guard;
                int index = -1;

                @Override
                public boolean hasNext() {
                    return cur.next != guard;
                }

                @Override
                public T next() {
                    return (T) (cur = cur.next);
                }

                @Override
                public boolean hasPrevious() {
                    return index > 0;
                }

                @Override
                public T previous() {
                    return (T) (cur = cur.previous);
                }

                @Override
                public int nextIndex() {
                    return index + 1;
                }

                @Override
                public int previousIndex() {
                    return index - 1;
                }

                @Override
                public void remove() {
                    index--;
                    T t = (T) cur;
                    cur = cur.previous;
                    removeFrom(t);
                }

                @Override
                public void set(T t) {
                    addAfter(cur, t);
                    removeFrom(cur);
                    cur = t;
                }

                @Override
                public void add(T t) {
                    addAfter(cur, t);
                }
            };
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                LinkedNode cur = guard;

                @Override
                public boolean hasNext() {
                    return cur.next != guard;
                }

                @Override
                public T next() {
                    return (T) (cur = cur.next);
                }
            };
        }

        public static class LinkedNode {
            public LinkedNode previous;
            public LinkedNode next;
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