package com.daltao.template;

import java.util.Iterator;

public class LinkedList<T extends LinkedList.LinkedNode> implements Iterable<T>, Iterator<T> {
    LinkedNode head;
    int id;
    int size;
    LinkedNode iter;

    public LinkedList(int id) {
        this.id = id;
        head = new LinkedNode(id + 1);
        head.next[id] = head.prev[id] = head;
    }

    @Override
    public Iterator<T> iterator() {
        iter = head.next[id];
        return this;
    }

    @Override
    public boolean hasNext() {
        return iter != head;
    }

    @Override
    public T next() {
        T t = (T) iter;
        iter = iter.next[id];
        return t;
    }

    public boolean isEmpty() {
        return head.next[id] == head;
    }

    public T removeLast() {
        T last = (T) head.prev[id];
        remove(last);
        size--;
        return last;
    }

    public void remove(T node) {
        node.prev[id].next[id] = node.next[id];
        node.next[id].prev[id] = node.prev[id];
    }

    public int size() {
        return size;
    }

    public void addLast(T node) {
        addAfter(head.prev[id], node);
        size++;
    }

    private void addAfter(LinkedNode former, LinkedNode later) {
        later.next[id] = former.next[id];
        former.next[id].prev[id] = later;

        later.prev[id] = former;
        former.next[id] = later;
    }

    public T removeFirst() {
        T first = getFirst();
        remove(first);
        size--;
        return first;
    }

    public T getFirst() {
        return (T) head.next[id];
    }

    public void addFirst(T e) {
        addAfter(head, e);
        size++;
    }

    public T getLast() {
        return (T) head.prev[id];
    }

    public static class LinkedNode {
        LinkedNode[] prev;
        LinkedNode[] next;

        public LinkedNode(int cap) {
            this.prev = new LinkedNode[cap];
            this.next = new LinkedNode[cap];
        }
    }

}