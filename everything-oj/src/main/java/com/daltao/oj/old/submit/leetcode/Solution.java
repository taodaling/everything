package com.daltao.oj.old.submit.leetcode;

class ShortestPalindrome {

    public static void main(String[] args) {
        ShortestPalindrome solution = new ShortestPalindrome();
        System.out.println(solution.shortestPalindrome("1"));
        System.out.println(solution.shortestPalindrome(""));
        System.out.println(solution.shortestPalindrome("12"));
        System.out.println(solution.shortestPalindrome("112"));
        System.out.println(solution.shortestPalindrome("122"));
        System.out.println(solution.shortestPalindrome("32123"));
        System.out.println(solution.shortestPalindrome("abcd"));
        System.out.println(solution.shortestPalindrome("abbabaab"));
    }

    public String shortestPalindrome(String s) {
        if (s.length() == 0) {
            return s;
        }
        int n = s.length();
        PAM pam = new PAM(n);
        for (int i = n - 1; i >= 0; i--) {
            pam.build(s.charAt(i));
        }

        int longest = pam.last.length;

        StringBuilder builder = new StringBuilder();
        builder.append(s, longest, n);
        builder.reverse();
        builder.append(s);
        return builder.toString();
    }

    public static class PAM {
        Node even;
        Node odd;
        Node last;
        char[] data;
        int top;

        public PAM(int cap) {
            data = new char[cap];
            top = -1;

            odd = new Node();
            odd.length = -1;
            even = new Node();
            even.length = 0;
            even.fail = odd;

            last = odd;
        }

        public void build(char c) {
            data[++top] = c;
            while (top - last.length <= 0) {
                last = last.fail;
            }
            while (data[top - last.length - 1] != c) {
                last = last.fail;
            }
            if (last.next[c] != null) {
                last = last.next[c];
                return;
            }

            Node now = new Node();
            now.length = last.length + 2;
            last.next[c] = now;

            if (now.length == 1) {
                now.fail = even;
            } else {
                Node trace = last.fail;
                while (data[top - trace.length - 1] != c) {
                    trace = trace.fail;
                }
                now.fail = trace.next[c];
            }

            last = now;
        }

        public static class Node {
            Node[] next = new Node[128];
            Node fail;
            int length;
        }


    }
}