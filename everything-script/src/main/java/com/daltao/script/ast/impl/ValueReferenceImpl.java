package com.daltao.script.ast.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ValueReference;

import java.util.Objects;

public class ValueReferenceImpl<T> implements ValueReference<T> {
    private T value;
    private ASTContext context;

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public ASTContext getContext() {
        return context;
    }

    @Override
    public void setContext(ASTContext context) {
        this.context = context;
    }

    @Override
    public ValueReference newForwardingValueReferenceInContext(ASTContext context) {
        return new ForwardingValueReference(this);
    }

    private static class ForwardingValueReference implements ValueReference {
        private ValueReferenceImpl delegate;

        private ForwardingValueReference(ValueReferenceImpl delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object getValue() {
            return delegate.getValue();
        }

        @Override
        public void setValue(Object value) {
            delegate.setValue(value);
        }

        @Override
        public ASTContext getContext() {
            return delegate.getContext();
        }

        @Override
        public void setContext(ASTContext context) {
            delegate.setContext(context);
        }

        @Override
        public ValueReference newForwardingValueReferenceInContext(ASTContext context) {
            return delegate.newForwardingValueReferenceInContext(context);
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }

    @Override
    public String toString() {
        return "" + Objects.hashCode(value);
    }
}
