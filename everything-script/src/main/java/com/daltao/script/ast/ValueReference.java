package com.daltao.script.ast;


import lombok.Data;

public interface ValueReference<T> {
    T getValue();
    void setValue(T value);

    ASTContext getContext();
    void setContext(ASTContext context);

    ValueReference newForwardingValueReferenceInContext(ASTContext context);
}
