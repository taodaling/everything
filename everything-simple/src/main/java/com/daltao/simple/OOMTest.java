package com.daltao.simple;

import java.util.ArrayList;
import java.util.List;

public class OOMTest {
    public static void main(String[] args) {
        try {
            List<Object> list = new ArrayList<>();
            while (true) {
                list.add(new int[100000000]);
            }
        } catch (Throwable t) {
            System.out.println("catch exception");
            t.printStackTrace();
        }

    }
}
