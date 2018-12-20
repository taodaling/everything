package com.daltao.collection;

import java.util.Deque;

public abstract class ForwardingDeque<E> implements Deque<E> {
    protected abstract Deque<E> delegate();

    public boolean add(E arg0) {
        return delegate().add(arg0);
    }

    public E remove() {
        return delegate().remove();
    }

    public boolean remove(java.lang.Object arg0) {
        return delegate().remove(arg0);
    }

    public boolean contains(java.lang.Object arg0) {
        return delegate().contains(arg0);
    }

    public java.util.Iterator<E> iterator() {
        return delegate().iterator();
    }

    public int size() {
        return delegate().size();
    }

    public E getFirst() {
        return delegate().getFirst();
    }

    public E pop() {
        return delegate().pop();
    }

    public void push(E arg0) {
        delegate().push(arg0);
    }

    public E poll() {
        return delegate().poll();
    }

    public E peek() {
        return delegate().peek();
    }

    public void addFirst(E arg0) {
        delegate().addFirst(arg0);
    }

    public void addLast(E arg0) {
        delegate().addLast(arg0);
    }

    public java.util.Iterator<E> descendingIterator() {
        return delegate().descendingIterator();
    }

    public E element() {
        return delegate().element();
    }

    public E getLast() {
        return delegate().getLast();
    }

    public boolean offer(E arg0) {
        return delegate().offer(arg0);
    }

    public boolean offerFirst(E arg0) {
        return delegate().offerFirst(arg0);
    }

    public boolean offerLast(E arg0) {
        return delegate().offerLast(arg0);
    }

    public E peekFirst() {
        return delegate().peekFirst();
    }

    public E peekLast() {
        return delegate().peekLast();
    }

    public E pollFirst() {
        return delegate().pollFirst();
    }

    public E pollLast() {
        return delegate().pollLast();
    }

    public E removeFirst() {
        return delegate().removeFirst();
    }

    public boolean removeFirstOccurrence(java.lang.Object arg0) {
        return delegate().removeFirstOccurrence(arg0);
    }

    public E removeLast() {
        return delegate().removeLast();
    }

    public boolean removeLastOccurrence(java.lang.Object arg0) {
        return delegate().removeLastOccurrence(arg0);
    }

    public boolean equals(java.lang.Object arg0) {
        return delegate().equals(arg0);
    }

    public int hashCode() {
        return delegate().hashCode();
    }

    public void clear() {
        delegate().clear();
    }

    public boolean isEmpty() {
        return delegate().isEmpty();
    }

    public <T> T[] toArray(T[] arg0) {
        return delegate().toArray(arg0);
    }

    public java.lang.Object[] toArray() {
        return delegate().toArray();
    }

    public java.util.Spliterator<E> spliterator() {
        return delegate().spliterator();
    }

    public boolean addAll(java.util.Collection<? extends E> arg0) {
        return delegate().addAll(arg0);
    }

    public java.util.stream.Stream<E> stream() {
        return delegate().stream();
    }

    public boolean containsAll(java.util.Collection<?> arg0) {
        return delegate().containsAll(arg0);
    }

    public boolean removeAll(java.util.Collection<?> arg0) {
        return delegate().removeAll(arg0);
    }

    public boolean removeIf(java.util.function.Predicate<? super E> arg0) {
        return delegate().removeIf(arg0);
    }

    public boolean retainAll(java.util.Collection<?> arg0) {
        return delegate().retainAll(arg0);
    }

    public java.util.stream.Stream<E> parallelStream() {
        return delegate().parallelStream();
    }

    public void forEach(java.util.function.Consumer<? super E> arg0) {
        delegate().forEach(arg0);
    }
}