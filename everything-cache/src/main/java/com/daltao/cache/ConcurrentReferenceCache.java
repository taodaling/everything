package com.daltao.cache;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentReferenceCache<K, V> implements Cache<K, V> {
    private static final int REGION_NUMBER = 16;
    private Reference<Map<K, V>>[] references;
    private volatile Reference volatileObject = null;
    private Object mutex = new Object();

    public ConcurrentReferenceCache() {
        references = new Reference[REGION_NUMBER];
        Arrays.fill(references, new SoftReference<>(null));
    }

    private Map<K, V> getStorage(K key) {
        int h = key.hashCode() & (REGION_NUMBER - 1);
        Map<K, V> map = references[h].get();
        if (map == null) {
            synchronized (mutex) {
                map = references[h].get();
                if (map == null) {
                    volatileObject = new SoftReference(new ConcurrentHashMap<>(1));
                    references[h] = volatileObject;
                    map = (Map<K, V>) volatileObject;
                }
            }
        }
        return map;
    }

    @Override
    public boolean contain(K key) {
        return getStorage(key).containsKey(key);
    }

    @Override
    public V get(K key) {
        return getStorage(key).get(key);
    }

    @Override
    public void add(K key, V value) {
        getStorage(key).put(key, value);
    }

    @Override
    public void purge(K key) {
        getStorage(key).remove(key);
    }

    @Override
    public void purgeAll() {
        Arrays.fill(references, new SoftReference<>(null));
    }
}
