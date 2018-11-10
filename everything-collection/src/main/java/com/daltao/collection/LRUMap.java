package com.daltao.collection;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUMap<K, V> extends LinkedHashMap<K, V> {
    private final int limitation;

    public LRUMap(int limitation) {
        super(limitation);
        this.limitation = limitation;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > limitation;
    }
}
