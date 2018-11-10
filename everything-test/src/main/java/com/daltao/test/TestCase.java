package com.daltao.test;

import com.daltao.utils.StringUtils;
import lombok.Getter;

import java.util.Arrays;

@Getter
public final class TestCase {
    private Object type;
    private Object[] args;

    public static TestCase newTestCase(Object type, Object... args) {
        TestCase testCase = new TestCase();
        testCase.type = type;
        testCase.args = args;
        return testCase;
    }



    @Override
    public String toString() {
        return type.toString() + "(" + StringUtils.concatenate(",", args) + ")";
    }
}
