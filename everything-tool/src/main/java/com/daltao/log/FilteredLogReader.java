package com.daltao.log;

import com.daltao.collection.AbstractIterator;

import java.util.Iterator;
import java.util.function.Predicate;

public class FilteredLogReader extends AbstractIterator<Log> {
    private final Iterator<Log> iterator;
    private final Predicate<Log> predicate;

    public FilteredLogReader(Iterator<Log> iterator, Predicate<Log> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }

    @Override
    protected Log next0() {
        while (iterator.hasNext()) {
            Log next = iterator.next();
            if (predicate.test(next)) {
                return next;
            }
        }
        return end();
    }
}
