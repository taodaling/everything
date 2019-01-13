package com.daltao.framework.signalstatemachine;

public interface SignalHandler {
    public void receive(StateMachine machine, Signal signal);
}