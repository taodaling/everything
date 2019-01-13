package com.daltao.framework.statemachine;

public interface StateMachine {
    public void switchState(State state);

    public void handle();
}
