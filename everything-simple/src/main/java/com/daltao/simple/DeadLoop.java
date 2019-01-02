package com.daltao.simple;

import java.util.HashMap;
import java.util.WeakHashMap;

public class DeadLoop {
    public static void main(String[] args) {
        WeakHashMap map = new WeakHashMap();
        while(true) {
            map.put(new Object(), new Object());
        }
    }
}
