package com.daltao.cache;

import java.util.Map;
import java.util.Objects;

public class Map2CacheAdapter<K, V> implements Cache<K, V> {
    private final Map<K, V> map;

    public Map2CacheAdapter(Map<K, V> map) {
        this.map = Objects.requireNonNull(map);
    }

    @Override
    public void add(K key, V value) {
        map.put(key, value);
    }

    @Override
    public boolean contain(K key) {
        return map.containsKey(key);
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public void purge(K key) {
        map.remove(key);
    }

    @Override
    public void purgeAll() {
        map.clear();
    }
}
