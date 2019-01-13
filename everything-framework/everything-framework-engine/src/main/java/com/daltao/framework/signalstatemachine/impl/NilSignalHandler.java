package com.daltao.framework.signalstatemachine.impl;

import com.daltao.framework.signalstatemachine.Signal;
import com.daltao.framework.signalstatemachine.SignalHandler;
import com.daltao.framework.signalstatemachine.StateMachine;

public class NilSignalHandler implements SignalHandler {
    private static final NilSignalHandler INSTANCE = new NilSignalHandler();

    private NilSignalHandler() {
    }

    public static NilSignalHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void receive(StateMachine machine, Signal signal) {
    }
}
