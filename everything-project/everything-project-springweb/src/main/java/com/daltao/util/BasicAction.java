package com.daltao.util;

import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BasicAction<I, O> implements Action<I, O> {
    private List<ActionListener<I, O>> listeners =
            new CopyOnWriteArrayList<>();

    @Override
    public final O invoke(I input) {
        preAction(input);
        O output = invoke0(input);
        postAction(output);
        return output;
    }

    public final void preAction(I input) {
        for (ActionListener<I, O> actionListener : listeners) {
            actionListener.preAction(this, input);
        }
    }

    public final void postAction(O output) {
        for (ActionListener<I, O> actionListener : listeners) {
            try {
                actionListener.postAction(this, output);
            } catch (Throwable t) {
                LoggerFactory.getLogger(this.getClass()).error("Unexpected exception", t);
            }
        }
    }

    public abstract O invoke0(I input);

    @Override
    public final BasicAction<I, O> addListener(ActionListener<I, O> listener) {
        listeners.add(listener);
        listener.registered(this);
        return this;
    }

    @Override
    public final BasicAction<I, O> removeListener(ActionListener<I, O> listener) {
        if (listeners.remove(listener)) {
            listener.removed(this);
        }
        return this;
    }
}
