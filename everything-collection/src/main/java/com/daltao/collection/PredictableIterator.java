package com.daltao.collection;

import java.util.Iterator;

public class PredictableIterator<E> implements Iterator<E> {
    private Iterator<E> iterator;
    private FixedArrayDeque<E> deque;

    public PredictableIterator(Iterator<E> iterator, int distance) {
        this.iterator = iterator;
        deque = new FixedArrayDeque<>(distance);

        for (int i = 0; i < distance && iterator.hasNext(); i++) {
            deque.addLast(iterator.next());
        }
    }

    @Override
    public boolean hasNext() {
        return !deque.isEmpty();
    }

    public E peek(int i) {
        return deque.peekRelatedToHead(i);
    }

    @Override
    public E next() {
        E result = deque.removeFirst();
        if (iterator.hasNext()) {
            deque.addLast(iterator.next());
        }
        return result;
    }
}
