package com.daltao.collection.wrapper;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class MapWrapper<K, V> implements Map<K, V> {
    protected Map<K, V> inner;

    public MapWrapper(Map<K, V> a0) {
        if (a0 == null) {
            throw new NullPointerException();
        }
        this.inner = a0;
    }

    @Override
    public V remove(Object a0) {
        return inner.remove(a0);
    }

    @Override
    public boolean remove(Object a0, Object a1) {
        return inner.remove(a0, a1);
    }

    @Override
    public V get(Object a0) {
        return inner.get(a0);
    }

    @Override
    public V put(K a0, V a1) {
        return inner.put(a0, a1);
    }

    @Override
    public boolean equals(Object a0) {
        return inner.equals(a0);
    }

    @Override
    public Collection<V> values() {
        return inner.values();
    }

    @Override
    public int hashCode() {
        return inner.hashCode();
    }

    @Override
    public void clear() {
        inner.clear();
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public V replace(K a0, V a1) {
        return inner.replace(a0, a1);
    }

    @Override
    public boolean replace(K a0, V a1, V a2) {
        return inner.replace(a0, a1, a2);
    }

    @Override
    public void replaceAll(BiFunction a0) {
        inner.replaceAll(a0);
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public Set entrySet() {
        return inner.entrySet();
    }

    @Override
    public void putAll(Map a0) {
        inner.putAll(a0);
    }

    @Override
    public V putIfAbsent(K a0, V a1) {
        return inner.putIfAbsent(a0, a1);
    }

    @Override
    public Set<K> keySet() {
        return inner.keySet();
    }

    @Override
    public V compute(K a0,
                     BiFunction<? super K, ? super V, ? extends V> a1) {
        return inner.compute(a0, a1);
    }

    @Override
    public V computeIfAbsent(K a0,
                             Function<? super K, ? extends V> a1) {
        return inner.computeIfAbsent(a0, a1);
    }

    @Override
    public V computeIfPresent(K a0,
                              BiFunction<? super K, ? super V, ? extends V> a1) {
        return inner.computeIfPresent(a0, a1);
    }

    @Override
    public boolean containsKey(Object a0) {
        return inner.containsKey(a0);
    }

    @Override
    public boolean containsValue(Object a0) {
        return inner.containsValue(a0);
    }

    @Override
    public void forEach(BiConsumer a0) {
        inner.forEach(a0);
    }

    @Override
    public V getOrDefault(Object a0, V a1) {
        return inner.getOrDefault(a0, a1);
    }

    @Override
    public V merge(K a0, V a1,
                   BiFunction<? super V, ? super V, ? extends V> a2) {
        return inner.merge(a0, a1, a2);
    }
}