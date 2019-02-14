package com.daltao.log;

import com.google.common.collect.ForwardingIterator;

import java.util.Iterator;
import java.util.function.Function;

public class TransformedLogReader extends ForwardingIterator<String> {
    private final Iterator<String> iterator;
    private Function<String, String> function;

    public TransformedLogReader(Iterator<String> iterator, Function<String, String> function) {
        this.iterator = iterator;
        this.function = function;
    }

    @Override
    protected Iterator<String> delegate() {
        return iterator;
    }

    @Override
    public String next() {
        return function.apply(super.next());
    }
}
