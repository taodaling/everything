package com.daltao.test;

import com.daltao.template.FastIO;

public abstract class ErrorChecker implements Checker {
    @Override
    public final boolean check(Input expected, Input actual, Input input) {
        try {
            return checkError(new FastIO(new Input2InputStream(expected), null),
                    new FastIO(new Input2InputStream(actual), null),
                    new FastIO(new Input2InputStream(input), null));
        } catch (Exception e) {
            return false;
        }
    }

    public abstract boolean checkError(FastIO expected, FastIO actual, FastIO input) throws Exception;
}
