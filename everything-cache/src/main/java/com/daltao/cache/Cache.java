package com.daltao.cache;

public interface Cache<K, V> {
    /**
     * Whether contain a value for key
     */
    boolean contain(K key);

    /**
     * Get the corresponding value for key
     */
    V get(K key);

    /**
     * Add a new cached item
     */
    void add(K key, V value);

    /**
     * Remove cached item with key
     */
    void purge(K key);

    /**
     * Remove all cached item
     */
    void purgeAll();
}
