package com.daltao.oj.old.submit.leetcode;

import java.util.*;

class WordBreak2 {
    public static void main(String[] args) {
        System.out.println(new WordBreak2().wordBreak("catsanddog",
                Arrays.asList(new String[]{"cat", "cats", "and", "sand", "dog"})));
    }

    public List<String> wordBreak(String s, List<String> wordDict) {
        int n = s.length();
        List<List<String>>[] dp = new List[n + 1];

        Set<String> set = new HashSet<String>();
        set.addAll(wordDict);

        dp[0] = new ArrayList<>();
        dp[0].add(new ArrayList<>());
        for (int i = 1; i <= n; i++) {
            dp[i] = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                String sub = s.substring(j, i);
                if (!set.contains(sub)) {
                    continue;
                }
                for (List<String> list : dp[j]) {
                    dp[i].add(clone(list, sub));
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        List<String> result = new ArrayList();
        for (List<String> list : dp[n]) {
            result.add(toString(list, builder));
        }
        return result;
    }

    public List<String> clone(List<String> obj, String addition) {
        List<String> list = new ArrayList(obj.size() + 1);
        list.addAll(obj);
        list.add(addition);
        return list;
    }

    public String toString(List<String> list, StringBuilder builder) {
        builder.setLength(0);
        for (String part : list) {
            builder.append(part).append(' ');
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}