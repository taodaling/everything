package com.daltao.collection;

import com.daltao.utils.Precondition;

import java.util.Iterator;
import java.util.function.Consumer;

public abstract class AbstractIterator<E> implements Iterator<E> {

    private ConsumedStatus<E> consumedStatus = new ConsumedStatus<>();
    private ReadyStatus<E> readyStatus = new ReadyStatus<>();
    private EndStatus<E> endStatus = new EndStatus<>();
    private Status<E> status = consumedStatus;
    private E nextElement;
    private boolean end;

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void setStatus(Status<E> status) {
        this.status = status;
    }

    private ConsumedStatus<E> getConsumedStatus() {
        return consumedStatus;
    }

    private ReadyStatus<E> getReadyStatus() {
        return readyStatus;
    }

    private EndStatus<E> getEndStatus() {
        return endStatus;
    }

    private interface Status<E> {
        boolean hasNext();

        E next();
    }

    private class ConsumedStatus<E> implements Status<E> {
        @Override
        public boolean hasNext() {
            nextElement = next0();
            if (end) {
                setStatus(getEndStatus());
                return false;
            } else {
                setStatus(getReadyStatus());
                return true;
            }
        }

        @Override
        public E next() {
            Precondition.isTrue(hasNext());
            return (E) AbstractIterator.this.next();
        }
    }

    private class ReadyStatus<E> implements Status<E> {
        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public E next() {
            setStatus(getConsumedStatus());
            return (E) nextElement;
        }
    }

    private class EndStatus<E> implements Status<E> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            throw new UnsupportedOperationException();
        }
    }


    public final E end() {
        end = true;
        return null;
    }

    @Override
    public final boolean hasNext() {
        return status.hasNext();
    }

    @Override
    public final E next() {
        return status.next();
    }

    protected abstract E next0();
}
