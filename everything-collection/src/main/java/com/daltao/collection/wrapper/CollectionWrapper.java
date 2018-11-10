package com.daltao.collection.wrapper;

import java.util.Collection;
import java.util.Iterator;

public abstract class CollectionWrapper<V> implements Collection<V> {
    private Collection<V> inner;

    protected Collection<V> getInner() {
        return inner;
    }

    public CollectionWrapper(Collection<V> a0) {
        if (a0 == null) {
            throw new NullPointerException();
        }
        this.inner = a0;
    }

    @Override
    public boolean add(V a0) {
        return inner.add(a0);
    }

    @Override
    public boolean remove(Object a0) {
        return inner.remove(a0);
    }

    @Override
    public boolean equals(Object a0) {
        return inner.equals(a0);
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
    public boolean contains(Object a0) {
        return inner.contains(a0);
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public Iterator<V> iterator() {
        return inner.iterator();
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public <T> T[] toArray(T[] a0) {
        return inner.toArray(a0);
    }

    @Override
    public Object[] toArray() {
        return inner.toArray();
    }

    @Override
    public boolean addAll(Collection<? extends V> a0) {
        return inner.addAll(a0);
    }

    @Override
    public boolean containsAll(Collection<?> a0) {
        return inner.containsAll(a0);
    }

    @Override
    public boolean removeAll(Collection<?> a0) {
        return inner.removeAll(a0);
    }

    @Override
    public boolean retainAll(Collection<?> a0) {
        return inner.retainAll(a0);
    }
}
