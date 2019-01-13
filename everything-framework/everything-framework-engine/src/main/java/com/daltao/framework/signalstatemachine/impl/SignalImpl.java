package com.daltao.framework.signalstatemachine.impl;

import com.daltao.framework.signalstatemachine.Signal;

import java.util.LinkedHashMap;
import java.util.Map;

public class SignalImpl implements Signal {
    private String type;
    private Map<Object, Object> properties = new LinkedHashMap<>();

    public SignalImpl(String type) {
        this.type = type;
    }

    public SignalImpl addProperty(Object key, Object value) {
        properties.put(key, value);
        return this;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public Map<Object, Object> properties() {
        return properties;
    }
}
