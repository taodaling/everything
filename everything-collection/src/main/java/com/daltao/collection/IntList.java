package com.daltao.collection;

import java.util.Arrays;

public class IntList {
    private int[] data;
    private int size;

    private void ensureCapacity(int c) {
        if (c < data.length) {
            return;
        }
        int properCapacity = data.length;
        while (properCapacity < c) {
            properCapacity *= 2;
        }
        data = Arrays.copyOf(data, properCapacity);
    }

    public void clear() {
        size = 0;
    }

    public void add(int x) {
        ensureCapacity(size + 1);
        data[size++] = x;
    }

    public int size() {
        return size;
    }

    public void remove(int i) {
        size--;
        if (i < size) {
            System.arraycopy(data, i + 1, data, i, size - i);
        }
    }

    public int get(int i) {
        return data[i];
    }

    public void set(int i, int x) {
        data[i] = x;
    }

    public void trimTail(int from) {
        size = from;
    }
}
