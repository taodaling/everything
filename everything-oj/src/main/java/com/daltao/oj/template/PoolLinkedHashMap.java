package com.daltao.oj.template;

import java.util.*;
import java.util.function.Supplier;

public class PoolLinkedHashMap<K, V> extends AbstractMap<K, V> {
    public PoolLinkedHashMap(Supplier<Entry[]> slotSupplier, Supplier<Entry> entrySupplier, Hash hash, EqualChecker equalChecker, int bit) {
        this.slotSupplier = slotSupplier;
        this.entrySupplier = entrySupplier;
        this.hash = hash;
        this.equalChecker = equalChecker;

        if (this.slotSupplier == null) {
            this.slotSupplier = () -> new Entry[128];
        }
        if (this.entrySupplier == null) {
            this.entrySupplier = Entry::new;
        }
        if (this.hash == null) {
            this.hash = Object::hashCode;
        }
        if (this.equalChecker == null) {
            this.equalChecker = Objects::equals;
        }

        this.slots = this.slotSupplier.get();
        if ((1 << bit) > this.slots.length) {
            throw new IllegalArgumentException();
        }
        mask = (1 << bit) - 1;

        linkedListHead = this.entrySupplier.get();
        linkedListHead.linkedListFormer = linkedListHead;
        linkedListHead.linkedListNext = linkedListHead;
    }

    public static class Entry<K, V> implements Map.Entry<K, V> {
        private Entry linkedListNext;
        private Entry linkedListFormer;
        private Entry slotNext;
        private int h;
        private K key;
        private V value;

        public void clear() {
            linkedListNext = null;
            linkedListFormer = null;
            slotNext = null;
            key = null;
            value = null;
            h = 0;
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

    public static interface Hash<K> {
        int hash(K k);
    }

    public static interface EqualChecker<K> {
        boolean equal(K a, K b);
    }

    public static interface RewindableSupplier<T> extends Supplier<T> {
        void rewind();
    }

    @Override
    public V get(Object key) {
        return getOrDefault(key, null);
    }

    private Entry<K, V> searchSlot(int index, int h, Object key) {
        Entry<K, V> last = slots[index];
        while (last != null && (last.h != h || !equalChecker.equal(last.key, key))) {
            last = last.slotNext;
        }
        return last;
    }

    @Override
    public V put(K key, V value) {
        int h = hash(key);
        int slotNum = h & mask;

        Entry<K, V> entry = searchSlot(slotNum, h, key);
        if (entry != null) {
            V old = entry.value;
            entry.value = value;
            return old;
        }

        entry = new Entry<>();
        entry.value = value;
        entry.key = key;
        entry.h = h;

        entry.slotNext = slots[slotNum];
        slots[slotNum] = entry;

        linkedListHead.linkedListFormer.linkedListNext = entry;
        entry.linkedListFormer = linkedListHead.linkedListFormer;
        linkedListHead.linkedListFormer = entry;
        entry.linkedListNext = linkedListHead;

        return null;
    }

    @Override
    public V remove(Object key) {
        int h = hash(key);
        int slotNum = h & mask;

        Entry<K, V> last = null;
        Entry<K, V> entry = slots[slotNum];
        while (entry != null && (entry.h != h || !equalChecker.equal(entry.key, key))) {
            last = entry;
            entry = entry.slotNext;
        }

        if (entry == null) {
            return null;
        }

        //Remove from slot
        if (last == null) {
            slots[slotNum] = entry.slotNext;
        } else {
            last.slotNext = entry.slotNext;
        }

        //Remove from linkedlist
        entry.linkedListNext.linkedListFormer = entry.linkedListFormer;
        entry.linkedListFormer.linkedListNext = entry.linkedListNext;

        return entry.value;
    }

    private final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = hash.hash(key)) ^ (h >>> 16);
    }

    @Override
    public boolean containsKey(Object key) {
        int h = hash(key);
        int slotNum = h & mask;
        return searchSlot(slotNum, h, key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        Arrays.fill(slots, 0, mask + 1, null);
        size = 0;
        linkedListHead.linkedListNext = linkedListHead.linkedListFormer = linkedListHead;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (set == null) {
            set = new AbstractSet<Entry<K, V>>() {
                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return new Iterator<Entry<K, V>>() {
                        Entry<K, V> head = linkedListHead;

                        @Override
                        public boolean hasNext() {
                            return head.linkedListNext != linkedListHead;
                        }

                        @Override
                        public Entry<K, V> next() {
                            return head = head.linkedListNext;
                        }
                    };
                }

                @Override
                public int size() {
                    return size;
                }
            };
        }
        return (Set) set;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        int h = hash(key);
        int slotNum = h & mask;

        Entry<K, V> entry = searchSlot(slotNum, h, key);
        if (entry == null) {
            return defaultValue;
        }
        return entry.value;
    }

    private Entry[] slots;
    private Supplier<Entry[]> slotSupplier;
    private Supplier<Entry> entrySupplier;
    private Hash hash;
    private EqualChecker equalChecker;
    private int mask;
    private int size;
    private Entry<K, V> linkedListHead;
    private Set<Entry<K, V>> set;
}
