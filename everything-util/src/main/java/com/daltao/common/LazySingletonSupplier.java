package com.daltao.common;

public abstract class LazySingletonSupplier<T> implements Factory<T> {
    private volatile T cache;

    @Override
    public T newInstance() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = newInstance0();
                }
            }
        }
        return cache;
    }

    protected abstract T newInstance0();
}
