package com.daltao.script.ast.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ValueReference;

import java.util.HashMap;
import java.util.Map;

public class NestedContext implements ASTContext {
    private static final ASTContext DEFAULT_CONTEXT = new ASTContext() {
        @Override
        public ValueReference getProperty(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean definedProperty(Object key) {
            return false;
        }
    };
    private Map<Object, ValueReference> parameter = new HashMap<>();
    private ASTContext definedContext = DEFAULT_CONTEXT;

    public ValueReference newLocalContextVariable(Object key, Object value) {
        ValueReference reference = new ValueReferenceImpl();
        reference.setValue(value);
        parameter.put(key, reference);
        return reference;
    }

    private ValueReference loadFromDefinedContext(Object key) {
        ValueReference valueReference = definedContext.getProperty(key);
        valueReference = valueReference.newForwardingValueReferenceInContext(this);
        parameter.put(key, valueReference);
        return valueReference;
    }

    @Override
    public ValueReference getProperty(Object key) {
        if (!parameter.containsKey(key)) {
            if (definedContext.definedProperty(key)) {
                return loadFromDefinedContext(key);
            } else {
                return newLocalContextVariable(key, null);
            }
        }
        return parameter.get(key);
    }


    @Override
    public boolean definedProperty(Object key) {
        return parameter.containsKey(key) || definedContext.definedProperty(key);
    }

    public void setDefinedContext(ASTContext definedContext) {
        this.definedContext = definedContext;
    }
}
