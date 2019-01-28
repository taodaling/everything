package com.daltao.framework.signalstatemachine.impl;

import com.daltao.framework.signalstatemachine.Signal;

import java.util.HashMap;
import java.util.Map;

public class SignalImpl implements Signal {
    private String type;
    private Map<Object, Object> properties = new HashMap<>();

    private SignalImpl() {
    }

    public static class Builder {
        private SignalImpl instance = new SignalImpl();

        public Builder type(String type) {
            instance.type = type;
            return this;
        }

        public Builder property(Object key, Object value) {
            instance.properties.put(key, value);
            return this;
        }

        public Builder target(String value) {
            return property("target", value);
        }

        public Builder routeKey(String value) {
            return property("routeKey", value);
        }

        public SignalImpl build() {
            return instance;
        }
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
