package com.daltao.framework.signalstatemachine.impl;

import com.daltao.framework.signalstatemachine.Signal;
import com.daltao.framework.signalstatemachine.SignalHandler;
import com.daltao.framework.signalstatemachine.StateListener;
import com.daltao.framework.signalstatemachine.StateMachine;

import java.util.*;

/**
 * This is a simple implementation for StateMachine interface.
 * You aren't supposed to change StateListener or SignalHandler
 * holding by this any more after invoking switchState.
 */
public class StateMachineImpl implements StateMachine {
    private Set<String> states;
    private Map<String, Set<StateListener>> stateListeners;
    private Map<String, SignalHandler> signalHandlers;
    private Map<String, Map<String, SignalHandler>> stateSignalHandlers;
    private String current;
    private SignalHandler defaultSignalHandler;

    private StateMachineImpl() {
    }

    @Override
    public void receiveSignal(Signal signal) {
        SignalHandler signalHandler = stateSignalHandlers.get(current).get(signal.type());
        if (signalHandler == null) {
            signalHandler = signalHandlers.get(signal.type());
        }
        if (signalHandler == null) {
            signalHandler = defaultSignalHandler;
        }
        if (signalHandler != null) {
            signalHandler.receive(this, signal);
        }
    }


    private void ensureExistState(String id) {
        if (!states.contains(id)) {
            throw new IllegalArgumentException(id + " not exists");
        }
    }

    public static class Builder {
        private Set<String> states = new HashSet<>();
        private Map<String, Set<StateListener>> stateListeners = new HashMap<>();
        private Map<String, SignalHandler> signalHandlers = new HashMap<>();
        private String initState;
        private SignalHandler defaultSignalHandler = NilSignalHandler.getInstance();
        private Map<String, Map<String, SignalHandler>> stateSignalHandlers;

        {
            initState = "__init";
            newState(initState);
        }

        {
            signalHandlers.put("refresh", new RefreshSignalHandler());
            signalHandlers.put("jump", new JumpSignalHandler());
        }

        private void ensureExistState(String id) {
            if (!states.contains(id)) {
                throw new IllegalArgumentException(id + " not exists");
            }
        }

        public Builder listenToState(String id, StateListener listener) {
            ensureExistState(id);
            stateListeners.get(id).add(listener);
            return this;
        }

        public Builder registerGlobalSignalHandler(String type, SignalHandler signalHandler) {
            signalHandlers.put(type, signalHandler);
            return this;
        }

        public Builder registerStateSignalHandler(String state, String type, SignalHandler signalHandler) {
            ensureExistState(state);
            stateSignalHandlers.get(state).put(type, signalHandler);
            return this;
        }

        public Builder newState(String id) {
            if (states.contains(id)) {
                throw new IllegalStateException("Duplicate state " + id);
            }
            states.add(id);
            stateListeners.put(id, new LinkedHashSet<>());
            stateSignalHandlers.put(id, new HashMap<>());
            return this;
        }

        public StateMachineImpl build() {
            StateMachineImpl result = new StateMachineImpl();
            result.states = states;
            result.stateListeners = stateListeners;
            result.signalHandlers = signalHandlers;
            result.current = initState;
            result.defaultSignalHandler = defaultSignalHandler;
            result.stateSignalHandlers = stateSignalHandlers;
            return result;
        }

        public Builder setInitState(String initState) {
            this.initState = initState;
            return this;
        }

        @Override
        public String toString() {
            return build().toString();
        }
    }


    @Override
    public void switchState(String id) {
        ensureExistState(id);
        afterLeaving();
        current = id;
        afterEntering();
    }

    private void afterLeaving() {
        for (StateListener listener : stateListeners.get(current)) {
            listener.leave(this, current);
        }
    }

    private void afterEntering() {
        for (StateListener listener : stateListeners.get(current)) {
            listener.enter(this, current);
        }
    }

    @Override
    public String currentState() {
        return current;
    }

    @Override
    public String toString() {
        return states.toString();
    }
}
