package com.daltao.oj.topcoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Aaagmnrs {
    public String[] anagrams(String[] phrases) {
        Set<String> sets = new HashSet<>();
        List<String> ans = new ArrayList<>();
        for (String s : phrases) {
            String summary = summary(s);
            if (sets.contains(summary)) {
                continue;
            }
            sets.add(summary);
            ans.add(s);
        }
        return ans.toArray(new String[0]);
    }

    public String summary(String s) {
        char[] data = s.replaceAll(" ", "").toLowerCase().toCharArray();
        Arrays.sort(data);
        return String.valueOf(data);
    }
}
