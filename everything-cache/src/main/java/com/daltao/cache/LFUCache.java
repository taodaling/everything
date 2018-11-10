package com.daltao.cache;

import com.daltao.utils.MathUtils;
import com.daltao.utils.Precondition;

/**
 * <pre>
 * LFU, once the size reach the limitation, remove one element to spare space.
 * The least visited element will be remove(get, put or contain will update the last visited time).
 * If multiple elements satisfy it, then pick the guy whose last visited time is furthest.
 *
 * All methods provided by this class has O(1) expected time complexity.
 * </pre>
 */
public class LFUCache<K, V> implements Cache<K, V> {
    private static class Node<K, V> extends LinkedNode<Node<K, V>> {
        private int hash;
        private K key;
        private V value;
        private Node<K, V> front;
        private Node<K, V> back;
        private List bindList;

        @Override
        public String toString() {
            return "" + key + ":" + value;
        }
    }

    private static class List<K, V> extends LinkedNode<List<K, V>> {
        private int time;
        private Node<K, V> head;

        @Override
        public String toString() {
            return "timeline-" + time;
        }

        public void addNode(Node<K, V> node) {
            node.bindList = this;
            if (head == null) {
                head = node;
                head.asLoop();
                return;
            }
            node.insertAfter(head);
        }

        /**
         * Remove a node from list
         */
        public void removeNode(Node<K, V> node) {
            if (node == head) {
                head = node.prev;
                if (head == node) {
                    head = null;
                }
            }

            node.leave();
        }

        public boolean isEmpty() {
            return head == null;
        }
    }

    private Node<K, V>[] slots;
    private int mask;
    private Node<K, V> nodeCacheHead;
    private List<K, V> listCacheHead;
    private List<K, V> chainedList;
    private int size;
    private int limitation;

    public LFUCache(int limitation) {
        Precondition.ge(limitation, 1);

        chainedList = new List<>();
        chainedList.asLoop();
        nodeCacheHead = new Node<>();
        nodeCacheHead.asLoop();
        listCacheHead = new List<>();
        listCacheHead.asLoop();
        for (int i = 0; i <= limitation; i++) {
            new Node().insertAfter(nodeCacheHead);
            new List().insertAfter(listCacheHead);
        }

        int slotNum = (1 << MathUtils.ceilLog2(limitation + (limitation >> 1)));
        mask = slotNum - 1;
        this.limitation = limitation;
        slots = new Node[slotNum];
    }

    private void onAddElement(Node<K, V> node) {
        size++;

        //Ensure timeline 1
        if (chainedList.next.time != 1) {
            List list = listCacheHead.next;
            list.leave();
            list.insertAfter(chainedList);
            list.time = 1;
        }

        //Add into time line 1
        chainedList.next.addNode(node);
    }

    private List<K, V> newList() {
        List<K, V> list = listCacheHead.next;
        list.leave();
        return list;
    }

    private void releaseList(List list) {
        list.insertAfter(listCacheHead);
    }

    private void releaseNode(Node node) {
        node.insertAfter(nodeCacheHead);
    }

    private Node<K, V> newNode() {
        Node<K, V> node = nodeCacheHead.next;
        node.leave();
        return node;
    }

    private static int hash(Object k) {
        int h = k.hashCode();
        return h ^ (h >>> 8) ^ (h >>> 16) ^ (h >>> 24);
    }

    private void onTouchElement(Node<K, V> node) {
        List<K, V> bindList = node.bindList;

        //Ensure bindList.next.time == bindList.time + 1
        if (bindList.next.time != bindList.time + 1) {
            List list = newList();
            list.time = bindList.time + 1;
            list.insertAfter(bindList);
        }

        //Jump from bindList to bindList.next
        bindList.removeNode(node);
        bindList.next.addNode(node);

        if (bindList.isEmpty()) {
            //Recycle
            bindList.leave();
            releaseList(bindList);
        }
    }


    private Node<K, V> mapTryFetch(K key, int h, int slotIndex) {
        Node<K, V> head = slots[slotIndex];
        if (head == null) {
            return null;
        }

        Node trace = head;
        do {
            if (trace.hash == h && trace.key.equals(key)) {
                return trace;
            }
            trace = trace.back;
        } while (trace != head);

        return null;
    }

    private Node<K, V> mapGet(K key, V value) {
        int h = hash(key);
        int slotIndex = h & mask;
        Node<K, V> node = mapTryFetch(key, h, slotIndex);
        return node;
    }

    @Override
    public boolean contain(K key) {
        int h = hash(key);
        int slotIndex = h & mask;
        Node<K, V> node = mapTryFetch(key, h, slotIndex);
        if (node != null) {
            onTouchElement(node);
            return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        int h = hash(key);
        int slotIndex = h & mask;
        Node<K, V> node = mapTryFetch(key, h, slotIndex);
        if (node != null) {
            onTouchElement(node);
            return node.value;
        }
        return null;
    }

    @Override
    public void add(K key, V value) {
        int h = hash(key);
        int slotIndex = h & mask;
        Node<K, V> node = mapTryFetch(key, h, slotIndex);
        if (node != null) {
            //Update element
            node.value = value;
            onTouchElement(node);
            return;
        }

        if (size == limitation) {
            purgeEldestNode();
        }

        node = newNode();
        node.leave();
        node.key = key;
        node.value = value;
        node.hash = h;
        if (slots[slotIndex] == null) {
            slots[slotIndex] = node.back = node.front = node;
        } else {
            Node head = slots[slotIndex];
            head.back.front = node;
            node.back = head.back;
            head.back = node;
            node.front = head;
        }

        onAddElement(node);
    }

    private void purgeEldestNode() {
        //Remove the eldest node
        removeNode(chainedList.next.head);
    }

    /**
     * Remove a node actually
     */
    private void removeNode(Node<K, V> node) {
        size--;

        //Remove from slot
        int slotIndex = node.hash & mask;
        if (node == slots[slotIndex]) {
            if (node == node.back) {
                slots[slotIndex] = null;
            } else {
                slots[slotIndex] = node.back;
            }
        }
        node.front.back = node.back;
        node.back.front = node.front;

        //Remove from timeline
        node.bindList.removeNode(node);
        if (node.bindList.isEmpty()) {
            //Recycle list
            node.bindList.leave();
            releaseList(node.bindList);
        }

        //Recycle node
        releaseNode(node);
    }

    @Override
    public void purge(K key) {
        int h = hash(key);
        int slotIndex = h & mask;
        Node<K, V> node = mapTryFetch(key, h, slotIndex);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    @Override
    public void purgeAll() {
        while (chainedList.next != chainedList) {
            List list = chainedList.next;
            while (list.head != null) {
                Node head = list.head;
                list.removeNode(head);
                releaseNode(head);
                int slotIndex = head.hash & mask;
                slots[slotIndex] = null;
            }
            list.leave();
            releaseList(list);
        }
        size = 0;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (Node<K, V> node : slots) {
            if (node == null) {
                continue;
            }
            Node trace = node;
            do {
                builder.append(trace.key).append(":").append(trace.value).append(":").append(trace.bindList.time)
                        .append(", ");
                trace = trace.back;
            } while (trace != node);
        }
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        }
        builder.append("}");
        return builder.toString();
    }
}
