package com.daltao.cache;

import com.daltao.collection.LRUMap;

import java.util.Map;

public class LRUCache<K, V> extends Map2CacheAdapter<K, V> {
    public LRUCache(int limitation) {
        super(new LRUMap(limitation));
    }
}
