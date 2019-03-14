package com.daltao.test;

public interface Checker {
    /**
     * Should expect and actual contain same data stream
     * @param expected
     * @param actual
     * @return
     */
    boolean check(Input expected, Input actual, Input input);
}