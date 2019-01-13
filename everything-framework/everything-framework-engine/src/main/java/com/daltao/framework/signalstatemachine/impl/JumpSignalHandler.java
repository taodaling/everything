package com.daltao.framework.signalstatemachine.impl;

import com.daltao.framework.signalstatemachine.Signal;
import com.daltao.framework.signalstatemachine.SignalHandler;
import com.daltao.framework.signalstatemachine.StateMachine;

public class JumpSignalHandler implements SignalHandler {
    @Override
    public void receive(StateMachine machine, Signal signal) {
        machine.switchState((String) signal.properties().get("target"));
    }
}
