package com.daltao.oj.old.submit.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/6/19.
 */
public class LetterCombinationsofaPhoneNumber {
    public static void main(String[] args)
    {
        new LetterCombinationsofaPhoneNumber().letterCombinations("23");
    }
    public static String[] wordSets = new String[]{
            "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
    };

    public List<String> letterCombinations(String digits) {
        List<String> result = Collections.emptyList();
        if (digits.length() > 0) {
            result = Arrays.asList("");
        }
        for (int i = 0, bound = digits.length(); i < bound; i++) {
            int c = digits.charAt(i) - '0';
            String wordSet = wordSets[c];
            List<String> newList = new ArrayList<>(result.size() * wordSet.length());
            for (int j = 0, jBound = wordSet.length(); j < jBound; j++) {
                char word = wordSet.charAt(j);
                for (String s : result) {
                    newList.add(s + word);
                }
            }
            result = newList;
        }
        return result;
    }
}
