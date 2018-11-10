package com.daltao.cache;

import java.util.function.Function;

public class LazyInitCache<K, V> implements Cache<K, V> {
    private Function<K, V> function;
    private Cache<K, V> cache;

    public LazyInitCache(Cache<K, V> cache, Function<K, V> function) {
        this.function = function;
        this.cache = cache;
    }

    @Override
    public boolean contain(K key) {
        return true;
    }

    @Override
    public V get(K key) {
        if (cache.contain(key)) {
            return cache.get(key);
        }
        V value = function.apply(key);
        cache.add(key, value);
        return value;
    }

    @Override
    public void add(K key, V value) {
        cache.add(key, value);
    }

    @Override
    public void purge(K key) {
        cache.purge(key);
    }

    @Override
    public void purgeAll() {
        cache.purgeAll();
    }
}
