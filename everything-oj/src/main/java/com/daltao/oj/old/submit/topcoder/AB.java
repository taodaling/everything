package com.daltao.oj.old.submit.topcoder;

public class AB{
    public static void main(String[] args)
    {
        new AB().createString(10, 12);
    }

    public String createString(int N, int K){
        int aNum = N / 2;
        int bNum = N - aNum;

        if((long)aNum * bNum < K)
        {
            return "";
        }

        int[] bBefore = new int[aNum + 1];

        long sum = (long)aNum * bNum;
        for(int i = aNum; i > 0 && sum > K; i--)
        {
            if(sum - bNum >= K)
            {
                bBefore[i] = bNum;
            }
            else
            {
                bBefore[i] = (int)(sum - K);
                sum = K;
            }
        }

        int bConsume = 0;
        StringBuilder builder = new StringBuilder(N);
        for(int i = 1; i <= aNum; i++)
        {
            while(bConsume < bBefore[i])
            {
                bConsume++;
                builder.append('B');
            }
            builder.append('A');
        }
        while(bConsume < bNum)
        {
            bConsume++;
            builder.append('B');
        }
        return builder.toString();
    }
}