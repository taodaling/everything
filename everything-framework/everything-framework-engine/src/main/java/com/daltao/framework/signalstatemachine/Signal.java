package com.daltao.framework.signalstatemachine;

import java.util.Map;

public interface Signal {
    public String type();
    public Map<Object, Object> properties();
}
