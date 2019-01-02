package com.daltao.util;

public interface Action<I, O> {
    O invoke(I input);

    Action<I, O> addListener(ActionListener<I, O> listener);

    Action<I, O> removeListener(ActionListener<I, O> listener);
}
