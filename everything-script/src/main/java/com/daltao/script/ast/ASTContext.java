package com.daltao.script.ast;

public interface ASTContext {
    Object getProperty(Object key);
    void putProperty(Object key, Object value);
    boolean definedProperty(Object key);
}
