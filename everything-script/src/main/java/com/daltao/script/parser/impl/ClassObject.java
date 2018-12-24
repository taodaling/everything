package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.ast.ValueReference;
import com.daltao.script.ast.impl.ValueReferenceImpl;

import java.util.HashMap;
import java.util.Map;

public class ClassObject implements ASTContext {
    private Map<Object, ValueReference> member = new HashMap<>();
    private ASTContext definedContext;

    {
        ValueReferenceImpl valueReference = new ValueReferenceImpl();
        valueReference.setContext(this);
        valueReference.setValue(this);
        member.put("this", valueReference);
    }

    @Override
    public ValueReference getProperty(Object key) {
        ValueReference reference = member.get(key);
        if (reference == null) {
            reference = new ValueReferenceImpl();
            reference.setContext(this);
            if (definedContext.definedProperty(key)) {
                reference.setValue(definedContext.getProperty(key).getValue());
            }
            member.put(key, reference);
        }
        return reference;
    }

    @Override
    public boolean definedProperty(Object key) {
        return member.containsKey(key) || definedContext.definedProperty(key);
    }

    public void setDefinedContext(ASTContext definedContext) {
        this.definedContext = definedContext;
    }

    @Override
    public String toString() {
        return "<" + definedContext.hashCode() + ">\n"
                + member.toString();
    }
}
