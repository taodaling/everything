package com.daltao.collection;

public class UnmodifiableArrayIterator<E> extends AbstractIterator<E> {
    private E[] data;
    private int from;
    private int to;

    public UnmodifiableArrayIterator(E[] data, int from, int to) {
        this.data = data;
        this.from = from;
        this.to = to;
    }

    @Override
    protected E next0() {
        if (from == to) {
            return end();
        }
        return data[from++];
    }
}
