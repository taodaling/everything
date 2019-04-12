package com.daltao.utils;

import com.daltao.template.Randomized;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtils {
    public static class MapBuilder<K, V> {
        private Map<K, V> map;

        public MapBuilder() {
            this(new HashMap<>());
        }

        public MapBuilder(Map<K, V> map) {
            this.map = map;
        }

        public MapBuilder<K, V> put(K k, V v) {
            map.put(k, v);
            return this;
        }

        public MapBuilder<K, V> remove(K k, V v) {
            map.remove(k, v);
            return this;
        }

        public MapBuilder<K, V> remove(K k) {
            map.remove(k);
            return this;
        }

        public Map<K, V> buildMapOnce() {
            Map<K, V> result = map;
            map = null;
            return result;
        }

        public Map<K, V> buildHashMap() {
            return new HashMap<>(map);
        }

        public Map<K, V> buildLinkedHashMap() {
            return new LinkedHashMap<>(map);
        }
    }

    public static <T> List<T> shuffle(List<T> list) {
        Object[] data = list.toArray();
        Randomized.randomizedArray(data, 0, data.length);
        return (List<T>) Arrays.asList(data);
    }
}
