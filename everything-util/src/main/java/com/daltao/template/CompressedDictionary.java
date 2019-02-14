package com.daltao.template;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CompressedDictionary<T> {
    Map<T, Integer> map;
    Map<Integer, T> rev;

    public CompressedDictionary(T[] data, Comparator<T> cmp, boolean needReverse) {
        data = data.clone();
        cmp = cmp;

        Arrays.sort(data, cmp);
        int n = data.length;

        map = new HashMap<>(n);
        map.put(data[0], 0);
        for (int i = 1; i < n; i++) {
            if (cmp.compare(data[i], data[i - 1]) == 0) {
                map.put(data[i], map.size() - 1);
            } else {
                map.put(data[i], map.size());
            }
        }

        if (needReverse) {
            rev = new HashMap<>(n);
            for (Map.Entry<T, Integer> entry : map.entrySet()) {
                rev.put(entry.getValue(), entry.getKey());
            }
        }
    }

    public Integer query(T val) {
        return map.get(val);
    }

    public T reverse(Integer val) {
        return rev.get(val);
    }
}
