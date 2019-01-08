package com.daltao.simple.rabbitmq;

import java.util.function.Consumer;

public abstract class RuntimeExceptionConsumer<T> implements Consumer<T> {
    @Override
    public void accept(T t) {
        try {
            accept0(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void accept0(T t) throws Exception;
}
