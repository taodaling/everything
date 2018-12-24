package com.daltao.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FunctionUtils {
    private static final Function<List, Object> FETCH_FIRST_ONE = list -> list.get(0);

    public static <E> Function<List<E>, E> fetchFirstOneFunction() {
        return (Function) FETCH_FIRST_ONE;
    }

    public static Function concatenate(Function... functions) {
        return new ConcatenateFunction(Arrays.asList(functions));
    }

    public static ConcatenateFunction newConcatenateFunction() {
        return new ConcatenateFunction();
    }

    public static class ConcatenateFunction implements Function {
        private List<Function> functions = new ArrayList<>();

        private ConcatenateFunction(List<Function> functions) {
            this.functions.addAll(functions);
        }

        public ConcatenateFunction() {
        }

        public ConcatenateFunction append(Function function) {
            this.functions.add(function);
            return this;
        }

        @Override
        public Object apply(Object o) {
            Object input = o;
            for (Function function : functions) {
                input = function.apply(input);
            }
            return input;
        }
    }
}
