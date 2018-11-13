package com.daltao.cache;

public abstract class LinkedNode<T extends LinkedNode<T>> {
    public T prev;
    public T next;

    public void insertAfter(T prev) {
        prev.next.prev = (T) this;
        this.next = prev.next;
        prev.next = (T) this;
        this.prev = prev;
    }

    public void insertBefore(T next) {
        insertAfter(next.prev);
    }

    public void leave() {
        prev.next = next;
        next.prev = prev;
    }

    public void asLoop() {
        this.prev = this.next = (T) this;
    }
}
