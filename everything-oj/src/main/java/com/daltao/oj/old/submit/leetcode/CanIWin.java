package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2018/6/1.
 */
public class CanIWin {
    public static void main(String[] args)
    {
        CanIWin canIWin = new CanIWin();
        System.out.println(canIWin.canIWin(10, 11));
    }

    int maxChoosableInteger;
    int desiredTotal;
    int[] dp;
    static final int ST_UNKNOW = 0;
    static final int ST_WIN = 3;
    static final int ST_DRAW = 2;
    static final int ST_LOSE = 1;

    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if((maxChoosableInteger + 1) * maxChoosableInteger / 2 < desiredTotal)
        {
            return false;
        }

        this.maxChoosableInteger = maxChoosableInteger;
        this.desiredTotal = desiredTotal;

        dp = new int[1 << maxChoosableInteger];

        return memsearch(dp.length - 1, 0) == ST_WIN;
    }

    public static int getBit(int mask, int bit)
    {
        return (mask >> bit) & 1;
    }

    public static int inverse(int status)
    {
        return 4 - status;
    }

    public int memsearch(int mask, int val)
    {
        if(dp[mask] == ST_UNKNOW)
        {
            if(val >= desiredTotal)
            {
                dp[mask] = ST_LOSE;
                return dp[mask];
            }

            dp[mask] = ST_LOSE;
            for(int i = 0; i < maxChoosableInteger && dp[mask] != ST_WIN; i++)
            {
                if(getBit(mask, i) == 0)
                {
                    continue;
                }
                dp[mask] = Math.max(dp[mask], inverse(memsearch(mask ^ (1 << i), val + i + 1)));
            }
        }

        return dp[mask];
    }
}
