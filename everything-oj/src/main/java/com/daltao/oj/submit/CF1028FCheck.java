package com.daltao.oj.submit;

import java.util.HashMap;
import java.util.Map;

public class CF1028FCheck {
    public static void main(String[] args) {
        Map<Long, Long> countMap = new HashMap<>();
        for (long i = 0; i <= 112904; i++) {
            System.out.println(i);
            for (long j = i; j <= 112904; j++) {
                long value = i * i + j * j;
                countMap.put(value, countMap.getOrDefault(value, 0L));
            }
        }

        long max = 0;
        for (Long v : countMap.values()) {
            max = Math.max(max, v);
        }

        System.out.println(max);
    }
}
