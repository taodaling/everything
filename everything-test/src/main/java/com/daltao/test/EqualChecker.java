package com.daltao.test;

import java.util.Objects;

public class EqualChecker implements Checker {
    private EqualChecker() {
    }

    private static EqualChecker instance = new EqualChecker();

    public static EqualChecker getInstance() {
        return instance;
    }

    @Override
    public boolean check(Input expected, Input actual, Input input) {
        while (expected.available() && actual.available()) {
            if (!Objects.equals(expected.read(), actual.read())) {
                return false;
            }
        }
        return expected.available() == actual.available();
    }
}
