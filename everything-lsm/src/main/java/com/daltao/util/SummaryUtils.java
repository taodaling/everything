package com.daltao.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SummaryUtils {
    private SummaryUtils() {
    }

    public static byte[] getSummary(byte[] key, byte[] value) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        digest.update(key);
        digest.update(value);
        return digest.digest();
    }

    public static boolean validate(byte[] key, byte[] value, byte[] summary) {
        return Arrays.equals(summary, getSummary(key, value));
    }
}
