package com.daltao.collection;

import com.daltao.common.Transactional;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class TransactionalRandomAccessListIterator<E> implements Iterator<E>, Transactional {
    private List<E> list;
    private int offset;
    private int size;
    private Deque<Integer> savePoints = new ArrayDeque<>();

    public TransactionalRandomAccessListIterator(List<E> list) {
        this.list = list;
        size = list.size();
    }

    public int currentOffset() {
        return offset;
    }

    @Override
    public boolean hasNext() {
        return offset < size;
    }

    @Override
    public E next() {
        return list.get(offset++);
    }

    @Override
    public Object savePoint() {
        savePoints.addLast(offset);
        return savePoints.peekLast();
    }

    @Override
    public void rollback(Object savePoint) {
        while (savePoints.removeLast() != savePoint) ;
        offset = (int) savePoint;
    }

    @Override
    public void commit(Object savePoint) {
        while (savePoints.removeLast() != savePoint) ;
    }
}
