package com.daltao.util;

import java.util.Iterator;

public class Iterators {
    private static Iterator emptyIterator = new Iterator() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new UnsupportedOperationException();
        }
    };


    public static <E> Iterator<E> emptyIterator() {
        return emptyIterator;
    }

}
