package com.daltao.oj.old.submit.leetcode;

/**
 * Created by Administrator on 2017/6/4.
 */
public class LongestSubstringWithoutRepeatingCharacters {
    public static void main(String[] args) {
        String s = "yiwgczzovxdrrgeebkqliobitcjgqxeqhbxkcyaxvdqplxtmhmarcbzwekewkknrnmdpmfohlfyweujlgjf";
        System.out.println(new LongestSubstringWithoutRepeatingCharacters().lengthOfLongestSubstring(s));
        for(char c = 0; c < 256; c++)
        {
            System.out.println((int)c + ":" + c);
        }
    }

    int[] nextOccurIndexes = null;
    int[] registries = new int[1 << 8];

    public int lengthOfLongestSubstring(String s) {
        //Calculate all next occur indexes
        //nextOccurIndexes[i] is the minimun index of s which has properties that index > i && s[i] = s[index]
        int slength = s.length();
        if (slength == 0) {
            return 0;
        }
        nextOccurIndexes = new int[s.length()];
        for (int i = 0, bound = registries.length; i < bound; i++) {
            registries[i] = -1;
        }
        for (int i = 0, bound = s.length(); i < bound; i++) {
            int c = s.charAt(i);
            int registry = registries[c];
            if (registry != -1) {
                nextOccurIndexes[registry] = i;
            }
            registries[c] = i;
        }
        for (int registry : registries) {
            if (registry != -1) {
                nextOccurIndexes[registry] = slength;
            }
        }

        int[] longestNoneRepetitionSubstringLengthes = new int[s.length()];
        longestNoneRepetitionSubstringLengthes[s.length() - 1] = 1;
        int longestNoneRepetitionSubstringIndex = s.length() - 1;
        for (int i = s.length() - 2; i >= 0; i--) {
            int probablyMaxLength1 = longestNoneRepetitionSubstringLengthes[i + 1] + 1;
            int probablyMaxLength2 = nextOccurIndexes[i] - i;
            longestNoneRepetitionSubstringLengthes[i] = Math.min(probablyMaxLength1, probablyMaxLength2);
            longestNoneRepetitionSubstringIndex =
                    longestNoneRepetitionSubstringLengthes[i] > longestNoneRepetitionSubstringLengthes[longestNoneRepetitionSubstringIndex] ?
                            i : longestNoneRepetitionSubstringIndex;
        }
        return longestNoneRepetitionSubstringLengthes[longestNoneRepetitionSubstringIndex];
    }
}
