package com.daltao.cache;

import java.util.HashMap;
import java.util.Map;

public class ExpirationCache<K, V> implements Cache<K, V> {

    /**
     * Use delegate as the bottom storage device, you should promise never share the delegate with others
     * because this instance might store private information in the delegate.
     * <br><br>
     * An item will expired if in the next expirePeriod millisecond it is not accessed.
     */
    public ExpirationCache(Cache delegate, long expirePeriod) {
        this.delegate = delegate;
        this.expirePeriod = expirePeriod;
    }

    private static final class Node<V> {
        private long expire;
        private V value;

        private Node(V value, long expire) {
            this.expire = expire;
            this.value = value;
        }
    }

    private Cache delegate;
    private long expirePeriod;

    @Override
    public boolean contain(K key) {
        Node<V> node = (Node<V>) delegate.get(key);
        if (node == null) {
            return false;
        }
        if (node.expire <= System.currentTimeMillis()) {
            purge(key);
            return false;
        }
        return true;
    }

    @Override
    public V get(K key) {
        Node<V> node = (Node<V>) delegate.get(key);
        if (node == null) {
            return null;
        }
        if (node.expire <= System.currentTimeMillis()) {
            purge(key);
            return null;
        }
        return node.value;
    }

    @Override
    public void add(K key, V value) {
        delegate.add(key, new Node(value, expirePeriod + System.currentTimeMillis()));
    }

    @Override
    public void purge(K key) {
        delegate.purge(key);
    }

    @Override
    public void purgeAll() {
        delegate.purgeAll();
    }
}
