package com.daltao.collection;

import com.daltao.utils.Precondition;

import java.util.Iterator;

public abstract class AbstractIterator<E> implements Iterator<E> {
    private static final Object END = new Object();

    private ConsumedStatus<E> consumedStatus = new ConsumedStatus<>();
    private ReadyStatus<E> readyStatus = new ReadyStatus<>();
    private EndStatus<E> endStatus = new EndStatus<>();
    private Status<E> status = consumedStatus;
    private E nextElement;

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
            if (nextElement == END) {
                setStatus(endStatus);
                return false;
            } else {
                setStatus(readyStatus);
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
            setStatus(consumedStatus);
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
        return (E) END;
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
