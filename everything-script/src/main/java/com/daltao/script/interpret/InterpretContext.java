package com.daltao.script.interpret;

public interface InterpretContext {
    Object getProperty(String name);

    void putProperty(String name, Object value);
}
