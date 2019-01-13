package com.daltao.framework.signalstatemachine;

public interface StateListener {
    public void enter(StateMachine machine, String state);

    public void leave(StateMachine machine, String state);
}
