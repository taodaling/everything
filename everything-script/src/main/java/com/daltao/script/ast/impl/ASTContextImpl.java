package com.daltao.script.ast.impl;

import com.daltao.script.ast.ASTContext;

import java.util.HashMap;
import java.util.Map;

public class ASTContextImpl implements ASTContext {
    private Map<Object, Object> properties = new HashMap<>();

    @Override
    public Object getProperty(Object key) {
        return properties.get(key);
    }

    @Override
    public void putProperty(Object key, Object value) {
        properties.put(key, value);
    }

    @Override
    public boolean definedProperty(Object key) {
        return properties.containsKey(key);
    }

    @Override
    public String toString() {
        return properties.toString();
    }
}
