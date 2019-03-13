package com.daltao.simple.twoway;

import java.util.Arrays;

public class TwoWay {
    public static void main(String[] args) {
        System.out.println(Arrays.deepToString(Arrays.stream(maxSuf("baac")).mapToObj(Integer::valueOf).toArray()));
    }

    public static int[] maxSuf(String s) {
        int kingIndex, challengerIndex, step, period;
        char challenger, oldKing;
        int len = s.length();
        char[] data = s.toCharArray();

        kingIndex = -1;
        challengerIndex = 0;
        step = period = 1;
        while (challengerIndex + step < len) {
            challenger = data[challengerIndex + step];
            oldKing = data[kingIndex + step];
            System.out.println((challengerIndex + step) + ":" + (kingIndex + step));
            if (challenger < oldKing) {
                challengerIndex += step;
                step = 1;
                period = challengerIndex - kingIndex;
            } else if (challenger == oldKing)
                if (step != period)
                    ++step;
                else {
                    challengerIndex += period;
                    step = 1;
                }
            else { /* oldKing is greater */
                kingIndex = challengerIndex;
                challengerIndex = kingIndex + 1;
                step = period = 1;
            }
        }
        return new int[]{kingIndex, period};
    }
}
