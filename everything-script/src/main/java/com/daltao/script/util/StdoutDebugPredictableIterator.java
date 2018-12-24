package com.daltao.script.util;

import com.daltao.collection.PredictableIterator;

import java.util.Iterator;

public class StdoutDebugPredictableIterator<E> extends PredictableIterator<E> {
    public StdoutDebugPredictableIterator(Iterator<E> iterator, int distance) {
        super(iterator, distance);
    }

    @Override
    public E next() {
        E result = super.next();
        //System.out.println("consumed " + result);
        return result;
    }
}
