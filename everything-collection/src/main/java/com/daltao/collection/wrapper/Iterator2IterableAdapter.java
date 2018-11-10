package com.daltao.collection.wrapper;

import java.util.Iterator;

/**
 * Created by dalt on 2018/5/27.
 */
public class Iterator2IterableAdapter<T> implements Iterable<T> {
    final Iterator<T> iterator;

    public Iterator2IterableAdapter(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }
}
