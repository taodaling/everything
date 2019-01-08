package com.daltao.oj.submit;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LTSolution {

    public static void main(String[] args) {
        System.out.println(new Solution()
        .isRationalEqual("9.99(999)", "10.00"));
    }


    static class Solution {
        public boolean isRationalEqual(String S, String T) {
            RationalNumber a = new RationalNumber(S);
            RationalNumber b = new RationalNumber(T);
            return RationalNumber.equal(a, b);
        }

        public static String repeat(String s, int k)
        {
            StringBuilder r = new StringBuilder();
            for(int i = 0; i < k; i++)
            {
                r.append(s);
            }
            return r.toString();
        }

        public static String addOne(String s)
        {
            int notNineIndex = s.length() - 1;
            while(notNineIndex >= 0 && s.charAt(notNineIndex) == '9')
            {
                notNineIndex--;
            }

            if(notNineIndex == -1)
            {
                return "1" + repeat("0", s.length());
            }

            return s.substring(0, notNineIndex) + (char)(s.charAt(notNineIndex) + 1) + s.substring(notNineIndex + 1, s.length());
        }

        public static class RationalNumber{
            String intPart = "";
            String nonRepPart = "";
            String repPart = "";

            public RationalNumber(String s)
            {
                int dotIndex = s.indexOf(".");
                if(dotIndex == -1)
                {
                    intPart = s;
                    return;
                }
                intPart = s.substring(0, dotIndex);
                s = s.substring(dotIndex + 1, s.length());

                int leftIndex = s.indexOf("(");
                if(leftIndex == -1)
                {
                    nonRepPart = s;
                    return;
                }

                nonRepPart = s.substring(0, leftIndex);
                repPart = s.substring(leftIndex + 1, s.length() - 1);

                if(repPart.length() == 4)
                {
                    String piece = repPart.substring(0,2);
                    if(repPart.equals(piece + piece))
                    {
                        repPart = piece;
                    }
                }
                if(repPart.length() == 3)
                {
                    String piece = repPart.substring(0,1);
                    if(repPart.equals(piece + piece + piece))
                    {
                        repPart = piece;
                    }
                }
                if(repPart.length() == 2)
                {
                    String piece = repPart.substring(0,1);
                    if(repPart.equals(piece + piece))
                    {
                        repPart = piece;
                    }
                }

                Deque<Character> deque = new ArrayDeque();
                for(char c : repPart.toCharArray())
                {
                    deque.addLast(c);
                }
                while(nonRepPart.endsWith(deque.getLast().toString()))
                {
                    nonRepPart = nonRepPart.substring(0, nonRepPart.length() - 1);
                    deque.addFirst(deque.removeLast());
                }

                repPart = "";
                while(!deque.isEmpty())
                {
                    repPart = repPart + deque.removeFirst();
                }

                if(repPart.equals("9"))
                {
                    repPart = "";
                    if(nonRepPart.length() > 0)
                    {
                        nonRepPart = addOne(nonRepPart);
                    }
                    else
                    {
                        intPart = addOne(intPart);
                    }
                }
            }

            public static boolean equal(RationalNumber a, RationalNumber b)
            {
                return a.intPart.equals(b.intPart) && a.nonRepPart.equals(b.nonRepPart) && a.repPart.equals(b.repPart);
            }

            @Override
            public String toString() {
                return intPart + "." + nonRepPart + "(" + repPart + ")";
            }
        }
    }

}
