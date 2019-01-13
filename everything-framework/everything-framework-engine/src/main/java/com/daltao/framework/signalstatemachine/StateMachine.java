package com.daltao.framework.signalstatemachine;

public interface StateMachine {
    public void switchState(String id);

    public String currentState();

    public void receiveSignal(Signal signal);
}
