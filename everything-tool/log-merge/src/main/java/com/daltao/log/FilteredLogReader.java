package com.daltao.log;

import com.daltao.collection.AbstractIterator;
import com.daltao.collection.IntList;

import java.util.Iterator;
import java.util.function.Predicate;

public class FilteredLogReader extends AbstractIterator<String> {
    private final Iterator<String> iterator;
    private final Predicate<String> predicate;

    public FilteredLogReader(Iterator<String> iterator, Predicate<String> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }

    @Override
    protected String next0() {
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (predicate.test(next)) {
                return next;
            }
        }
        return end();
    }
}
