package com.daltao.test;

import com.daltao.utils.Precondition;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public class QueueInput<T> implements Input<T> {
    private static final Object END = new Object();
    private Deque<T> deque = new LinkedList<>();

    @Override
    public T read() {
        Precondition.isTrue(available());
        return deque.removeFirst();
    }

    @Override
    public boolean available() {
        return deque.peekFirst() != END;
    }

    public QueueInput add(T data) {
        deque.addLast(data);
        return this;
    }

    public void end() {
        add((T) END);
    }
}