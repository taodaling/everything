package com.daltao.collection;

import java.util.AbstractCollection;
import java.util.Deque;

public abstract class AbstractDeque<T> extends AbstractCollection<T> implements Deque<T> {
    @Override
    public boolean offerFirst(T t) {
        addFirst(t);
        return true;
    }

    @Override
    public boolean offerLast(T t) {
        addLast(t);
        return true;
    }

    @Override
    public T pollFirst() {
        if (isEmpty()) {
            return null;
        }
        return removeFirst();
    }

    @Override
    public T pollLast() {
        if (isEmpty()) {
            return null;
        }
        return removeLast();
    }

    @Override
    public T peekFirst() {
        if (isEmpty()) {
            return null;
        }
        return getFirst();
    }

    @Override
    public T peekLast() {
        if (isEmpty()) {
            return null;
        }
        return getLast();
    }

    @Override
    public boolean offer(T t) {
        addLast(t);
        return true;
    }

    @Override
    public T poll() {
        if (isEmpty()) {
            return null;
        }
        return removeFirst();
    }

    @Override
    public T element() {
        return getFirst();
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return getFirst();
    }

    @Override
    public void push(T t) {
        addFirst(t);
    }

    @Override
    public T pop() {
        return removeFirst();
    }
}
