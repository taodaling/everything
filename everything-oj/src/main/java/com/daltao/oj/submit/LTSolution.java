package com.daltao.oj.submit;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LTSolution {

    public static void main(String[] args) {
        System.out.println(new Solution().shortestSuperstring(Arrays.asList("catg","ctaagt","gcta","ttca","atgcatc").toArray(new String[0])));
    }



    static class Solution {
        public String shortestSuperstring(String[] A) {
            n = A.length;
            mem = new int[1 << n][n];
            visited = new boolean[1 << n][n];
            last = new int[1 << n][n];
            profit = new int[n][n];

            for(int i = 0; i < n; i++)
            {
                for(int j = 0; j < n; j++)
                {
                    profit[i][j] = cover(A[i], A[j]);
                }
            }

            int maxIndex = 0;
            int maxValue = -1;
            int mask = (1 << n) - 1;
            for(int i = 0; i < n; i++)
            {
                int val = solve(mask, i);
                if(val > maxValue)
                {
                    maxValue = val;
                    maxIndex = i;
                }
            }

            System.out.println(maxValue);

            int lastIndex = maxIndex;
            Deque<Integer> deque = new ArrayDeque();
            int bits = mask;
            while(bits != 0)
            {
                deque.addFirst(lastIndex);
                int tempLastIndex = lastIndex;
                lastIndex = last[bits][lastIndex];
                bits &= ~(1 << tempLastIndex);
            }

            int lastKey = deque.removeFirst();
            StringBuilder builder = new StringBuilder(A[lastKey]);
            while(!deque.isEmpty())
            {
                int currentKey = deque.removeFirst();
                builder.setLength(builder.length() - profit[lastKey][currentKey]);
                builder.append(A[currentKey]);
                lastKey = currentKey;
            }

            return builder.toString();
        }

        int[][] mem;
        boolean[][] visited;
        int[][] last;
        int n;
        int[][] profit;

        private int solve(int bits, int b)
        {
            if(!visited[bits][b])
            {
                visited[bits][b] = true;

                int remainBits = bits & ~(1 << b);
                if(remainBits == 0)
                {
                    mem[bits][b] = 0;
                }
                else
                {
                    mem[bits][b] = -1;
                    last[bits][b] = -1;
                    for(int i = 0; i < n; i++)
                    {
                        if(((remainBits >> i) & 1) == 0)
                        {
                            continue;
                        }
                        int val = profit[i][b] + solve(remainBits, i);
                        if(val > mem[bits][b])
                        {
                            mem[bits][b] = val;
                            last[bits][b] = i;
                        }
                    }
                }
            }

            return mem[bits][b];
        }

        private static int cover(String a, String b)
        {
            for(int i = Math.min(a.length(), b.length()); i > 0; i--)
            {
                if(isCover(a, b, i))
                {
                    return i;
                }
            }
            return 0;
        }

        private static boolean isCover(String a, String b, int l)
        {
            for(int i = a.length() - l, j = 0, until = a.length(); i < until; i++, j++)
            {
                if(a.charAt(i) != b.charAt(j))
                {
                    return false;
                }
            }
            return true;
        }
    }

}
