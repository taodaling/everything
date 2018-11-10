package com.daltao.test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public class MultiDirectionInput<T> {
    private InputProxy<T>[] proxies;
    private Input<T> source;

    public MultiDirectionInput(Input<T> input, int num) {
        this.source = input;
        proxies = new InputProxy[num];
        for (int i = 0; i < num; i++) {
            proxies[i] = new InputProxy();
        }
    }

    public Input<T> getInput(int i) {
        return proxies[i];
    }

    private void dispatch() {
        T data = source.read();
        for (InputProxy proxy : proxies) {
            proxy.deque.addLast(data);
        }
    }


    private class InputProxy<T> implements Input<T> {
        private Deque<T> deque = new LinkedList<>();

        @Override
        public T read() {
            if (deque.isEmpty()) {
                dispatch();
            }
            return deque.removeFirst();
        }

        @Override
        public boolean available() {
            return !deque.isEmpty() || source.available();
        }
    }
}
