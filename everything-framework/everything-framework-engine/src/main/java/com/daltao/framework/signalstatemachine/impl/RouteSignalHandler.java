package com.daltao.framework.signalstatemachine.impl;

import com.daltao.framework.signalstatemachine.Signal;
import com.daltao.framework.signalstatemachine.SignalHandler;
import com.daltao.framework.signalstatemachine.StateMachine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RouteSignalHandler implements SignalHandler {
    private Map<String, Map<String, String>> routeMap = new HashMap<>();

    private Map<String, String> getRouteInfo(String src, boolean autoCreate) {
        Map<String, String> info = routeMap.get(src);
        if (info == null) {
            if (autoCreate) {
                info = new HashMap<>();
                routeMap.put(src, info);
            } else {
                info = Collections.emptyMap();
            }
        }
        return info;
    }

    public void buildEdge(String src, String target, String routeKey) {
        getRouteInfo(src, true).put(routeKey, target);
    }

    public void remove(String src, String target, String routeKey) {
        getRouteInfo(src, true).remove(routeKey, target);
    }

    @Override
    public void receive(StateMachine machine, Signal signal) {
        String routeKey = (String) signal.properties().get("routeKey");
        Map<String, String> routeInfo = getRouteInfo(machine.currentState(), false);
        if (!routeInfo.containsKey(routeKey)) {
            //No matching
            return;
        }
        machine.switchState(routeInfo.get(routeKey));
    }
}
