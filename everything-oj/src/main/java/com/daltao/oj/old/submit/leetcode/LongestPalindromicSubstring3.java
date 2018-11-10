package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2018/3/20.
 */
public class LongestPalindromicSubstring3 {
    public static void main(String[] args) {
//        System.out.println(new LongestPalindromicSubstring3().longestPalindrome("aba"));
//        System.out.println(new LongestPalindromicSubstring3().longestPalindrome("aabac"));
//        System.out.println(new LongestPalindromicSubstring3().longestPalindrome("abc"));
        System.out.println(new LongestPalindromicSubstring3().longestPalindrome("civilwartestingwhetherthatnaptionoranynartionsoconceivedandsodedicatedcanlongendureWeareqmetonagreatbattlefiemldoftzhatwarWehavecometodedicpateaportionofthatfieldasafinalrestingplaceforthosewhoheregavetheirlivesthatthatnationmightliveItisaltogetherfangandproperthatweshoulddothisButinalargersensewecannotdedicatewecannotconsecratewecannothallowthisgroundThebravelmenlivinganddeadwhostruggledherehaveconsecrateditfaraboveourpoorponwertoaddordetractTgheworldadswfilllittlenotlenorlongrememberwhatwesayherebutitcanneverforgetwhattheydidhereItisforusthelivingrathertobededicatedheretotheulnfinishedworkwhichtheywhofoughtherehavethusfarsonoblyadvancedItisratherforustobeherededicatedtothegreattdafskremainingbeforeusthatfromthesehonoreddeadwetakeincreaseddevotiontothatcauseforwhichtheygavethelastpfullmeasureofdevotionthatweherehighlyresolvethatthesedeadshallnothavediedinvainthatthisnationunsderGodshallhaveanewbirthoffreedomandthatgovernmentofthepeoplebythepeopleforthepeopleshallnotperishfromtheearth"));
    }

    public String longestPalindrome(String s) {
        if (s.length() == 0) {
            return "";
        }

        char[] data = s.toCharArray();

        PAM pam = new PAM(data.length);
        for (int i = 0, bound = data.length; i < bound; i++) {
            pam.build(data[i]);
        }

        return String.valueOf(data, pam.endIndex + 1 - pam.longestPalindrome, pam.longestPalindrome);
    }

    public static class PAM {
        char[] data;
        int top;
        Node even;
        Node odd;
        Node last;
        int longestPalindrome = 0;
        int endIndex = 0;

        public PAM(int cap) {
            data = new char[cap];
            top = -1;

            odd = new Node();
            odd.length = -1;
            last = odd;

            even = new Node();
            even.length = 0;
            even.fail = odd;
        }

        public void build(char c) {
            data[++top] = c;
            while (top - last.length <= 0) {
                last = last.fail;
            }
            while (data[top - last.length - 1] != c) {
                last = last.fail;
            }
            int index = c - 'A';
            if (last.nodes[index] != null) {
                last = last.nodes[index];
                return;
            }

            Node now = new Node();
            now.length = last.length + 2;
            last.nodes[index] = now;

            Node failTrace = last.fail;
            while (failTrace != null && data[top - failTrace.length - 1] != c) {
                failTrace = failTrace.fail;
            }
            now.fail = failTrace == null ? even : failTrace.nodes[index];

            last = now;
            if (longestPalindrome < now.length) {
                longestPalindrome = now.length;
                endIndex = top;
            }
        }

        static class Node {
            Node[] nodes = new Node[128];
            int length;
            Node fail;
        }
    }
}
