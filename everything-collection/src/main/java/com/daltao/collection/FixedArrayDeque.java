package com.daltao.collection;

import com.daltao.utils.MathUtils;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

public class FixedArrayDeque<E> extends AbstractDeque<E> implements Deque<E> {
    private int mask;
    private int size;
    private Object[] data;
    private int head;
    private int tail;

    public FixedArrayDeque(int capacity) {
        capacity = 1 << MathUtils.ceilLog2(capacity + 1);
        data = new Object[capacity];
        mask = capacity - 1;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int index = head;

            @Override
            public boolean hasNext() {
                return index != tail;
            }

            @Override
            public E next() {
                Object value = data[index];
                index = (index + 1) & mask;
                return (E) value;
            }
        };
    }

    public E peekRelatedToHead(int i) {
        return (E) data[(head + i) & mask];
    }

    public E peekRelatedToTail(int i) {
        return (E) data[(tail - 1 + i) & mask];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(E e) {
        data[head = (head - 1) & mask] = e;
        size++;
    }

    @Override
    public void addLast(E e) {
        data[tail] = e;
        tail = (tail + 1) & mask;
        size++;
    }

    @Override
    public E removeFirst() {
        E result = (E) data[head];
        data[head] = null;
        size--;
        head = (head + 1) & mask;
        return result;
    }

    @Override
    public E removeLast() {
        E result = (E) data[tail = (tail - 1) & mask];
        data[tail] = null;
        size--;
        return result;
    }

    @Override
    public E getFirst() {
        return (E) data[head];
    }

    @Override
    public E getLast() {
        return (E) data[(tail - 1) & mask];
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            int index = tail;

            @Override
            public boolean hasNext() {
                return index != head;
            }

            @Override
            public E next() {
                return (E) data[index = (index - 1) & mask];
            }
        };
    }
}
