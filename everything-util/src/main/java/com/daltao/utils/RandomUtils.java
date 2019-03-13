package com.daltao.utils;

import java.util.Random;

public class RandomUtils {
    public static int getRandomInt(Random random, int l, int r) {
        return random.nextInt(r - l + 1) + l;
    }

    public static char[] getRandomCharacterSequence(Random random, char l, char r, int len) {
        char[] data = new char[len];
        for (int i = 0; i < len; i++) {
            data[i] = (char) getRandomInt(random, l, r);
        }
        return data;
    }

    public static String getRandomString(Random random, char l, char r, int len) {
        return String.valueOf(getRandomCharacterSequence(random, l, r, len));
    }
}
