package com.daltao.util;

public interface ActionListener<I, O> {
    void preAction(Action<I, O> action, I param);
    void postAction(Action<I, O> action, O result);
    void registered(Action<I, O> action);
    void removed(Action<I, O> action);
}
