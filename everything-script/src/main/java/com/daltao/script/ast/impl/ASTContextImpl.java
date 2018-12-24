package com.daltao.script.ast.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ValueReference;

import java.util.HashMap;
import java.util.Map;

public class ASTContextImpl implements ASTContext {
    private Map<Object, ValueReference> properties = new HashMap<>();

    @Override
    public ValueReference getProperty(Object key) {
        ValueReference value = properties.get(key);
        if (value == null) {
            value = new ValueReferenceImpl();
            value.setValue(null);
            properties.put(key, value);
        }
        return value;
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
