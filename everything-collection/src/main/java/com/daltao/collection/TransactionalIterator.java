package com.daltao.collection;

import com.daltao.common.Transactional;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class TransactionalIterator<E> implements Iterator<E>, Transactional {
    private Deque<E> buffer = new LinkedList<>(); //stack
    private Deque<E> inQueue = new LinkedList<>(); //queue
    private Iterator<E> iterator;
    private Deque<Integer> savePoints = new ArrayDeque<>();

    @Override
    public Object savePoint() {
        savePoints.add(buffer.size());
        return savePoints.getLast();
    }

    @Override
    public void rollback(Object savePoint) {
        Integer savePoint0 = (Integer) savePoint;
        while (savePoints.removeLast() != savePoint0) ;
        while (buffer.size() > savePoint0) {
            inQueue.addFirst(buffer.removeLast());
        }
    }

    @Override
    public void commit(Object savePoint) {
        while (savePoints.removeLast() != savePoint) ;
        if (savePoints.isEmpty()) {
            buffer.clear();
        }
    }

    public TransactionalIterator(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return !inQueue.isEmpty() || iterator.hasNext();
    }

    public boolean inTransaction() {
        return !savePoints.isEmpty();
    }

    @Override
    public E next() {
        E item = inQueue.isEmpty() ? iterator.next() : inQueue.removeFirst();
        if (inTransaction()) {
            buffer.addLast(item);
        }
        return item;
    }
}
