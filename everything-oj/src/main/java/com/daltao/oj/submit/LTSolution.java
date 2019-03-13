package com.daltao.oj.submit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LTSolution {

    public static void main(String[] args) {

        System.out.println(new Solution()
                .mergeStones(new int[]{3, 2, 4, 1}, 2));
    }


    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    static class Solution {
        public static class Helper{
            int[][][] mem;
            int[] fee;
            boolean[][][] visited;
            int[] stones;
            int k;
            int n;
            static int inf = 100000000;
            public Helper(int[] stones, int k){
                this.stones = stones;
                this.k = k;
                n = stones.length;
                mem = new int[n][n][k + 1];
                visited = new boolean[n][n][k + 1];
                fee = new int[n + 1];
                fee[0] = 0;
                for(int i = 1; i <= n; i++)
                {
                    fee[i] = fee[i - 1] + stones[i - 1];
                }
            }

            public int getTotal(int l, int r)
            {
                return fee[r + 1] - fee[l];
            }

            public int dp(int l, int r, int p)
            {
                if(visited[l][r][p])
                {
                    return mem[l][r][p];
                }
                visited[l][r][p] = true;
                int len = r - l + 1;
                if(p == 0)
                {
                    mem[l][r][p] = inf;
                    return mem[l][r][p];
                }
                //unable to fetch
                if(len < p)
                {
                    mem[l][r][p] = inf;
                    return mem[l][r][p];
                }
                //equal case
                if(len == p)
                {
                    mem[l][r][p] = 0;
                    return mem[l][r][p];
                }
                //unable to merge
                if(len < k && p != len)
                {
                    mem[l][r][p] = inf;
                    return mem[l][r][p];
                }
                if(len == k && p == 1)
                {
                    mem[l][r][p] = getTotal(l, r);
                    return mem[l][r][p];
                }
                if(p == 1)
                {
                    mem[l][r][p] = getTotal(l, r) + dp(l, r, k);
                    return mem[l][r][p];
                }
                mem[l][r][p] = inf;
                for(int i = l; i <= r - 1; i++)
                {
                    for(int j = 1; j < p; j++)
                    {
                        mem[l][r][p] = Math.min(mem[l][r][p], dp(l, i, j) + dp(i + 1, r, p - j));
                    }
                }
                return mem[l][r][p];
            }
        }
        public int mergeStones(int[] stones, int K) {
            Helper helper = new Helper(stones, K);
            helper.dp(0, stones.length - 1, 2);
            int v = helper.dp(0, stones.length - 1, 1);
            return v == Helper.inf ? -1 : v;
        }
    }
}
