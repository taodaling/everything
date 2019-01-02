package com.daltao.util;

public class BasicActionListener<I, O> implements ActionListener<I, O> {
    @Override
    public void preAction(Action<I, O> action, I param) {

    }

    @Override
    public void postAction(Action<I, O> action, O result) {

    }

    @Override
    public void registered(Action<I, O> action) {

    }

    @Override
    public void removed(Action<I, O> action) {

    }
}
