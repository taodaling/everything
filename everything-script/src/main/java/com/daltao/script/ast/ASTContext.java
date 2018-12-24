package com.daltao.script.ast;

public interface ASTContext {
    ValueReference getProperty(Object key);

    boolean definedProperty(Object key);
}
