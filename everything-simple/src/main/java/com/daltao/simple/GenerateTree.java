package com.daltao.simple;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateTree {
    public static void main(String[] args) {
        Map root = new HashMap<>();
        root.put("children", new ArrayList<>());
        Map last = root;
        for (int i = 1; i <= 100; i++) {
            Map current = new HashMap();
            current.put("name", "x" + i);
            current.put("children", new ArrayList<>());

            ((List) last.get("children")).add(current);

            last = current;
        }

        System.out.println( new Gson().toJson(((List)root.get("children")).get(0)) );
    }
}
