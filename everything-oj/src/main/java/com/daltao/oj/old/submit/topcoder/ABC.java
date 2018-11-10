package com.daltao.oj.old.submit.topcoder;

public class ABC {
    public static void main(String[] args)
    {
        new ABC().createString(3, 0);
    }

    public String createString(int N, int K) {
        int aNum = N / 3;
        int bNum = (N - aNum) / 2;
        int cNum = N - aNum - bNum;
        int bcNum = bNum + cNum;

        int maxCnt = aNum * bcNum + bNum * cNum;
        if (maxCnt < K) {
            return "";
        }

        int[] gtA = new int[aNum];
        int[] gtB = new int[bNum];

        for (int i = bNum - 1; i >= 0 && maxCnt > K; i--) {
            gtB[i] = Math.min(cNum, maxCnt - K);
            maxCnt -= gtB[i];
        }

        for (int i = aNum - 1; i >= 0 && maxCnt > K; i--) {
            gtA[i] = Math.min(bcNum, maxCnt - K);
            maxCnt -= gtA[i];
        }

        int ai = 0;
        int bi = 0;
        int ci = 0;
        StringBuilder builder = new StringBuilder(N);
        for (int i = 0; i < N; i++) {
            if (ai < aNum && bi + ci == gtA[ai]) {
                builder.append('A');
                ai++;
            } else if (bi < bNum && ci == gtB[bi]) {
                builder.append('B');
                bi++;
            } else {
                builder.append('C');
                ci++;
            }
        }

        return builder.toString();
    }
}
