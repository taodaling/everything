package com.daltao.utils;

import java.util.List;
import java.util.function.Function;

public class FunctionUtils {
    private static final Function<List, Object> FETCH_FIRST_ONE = list -> list.get(0);

    public static <E> Function<List<E>, E> fetchFirstOneFunction() {
        return (Function) FETCH_FIRST_ONE;
    }


}
