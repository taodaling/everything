package com.daltao.common;

import java.util.function.Supplier;

public class SingletonFactory<T> implements Factory<T> {
    private T value;

    public SingletonFactory(T value) {
        this.value = value;
    }

    @Override
    public T newInstance() {
        return value;
    }
}
