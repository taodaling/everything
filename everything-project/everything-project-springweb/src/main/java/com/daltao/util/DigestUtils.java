package com.daltao.util;

import com.daltao.exception.UnexpectedException;
import com.daltao.message.Message;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {
    private static char[] radix16 = "0123456789abcdef".toCharArray();

    private DigestUtils() {
    }

    public static String asRadix16(byte[] data) {
        char[] output = new char[data.length * 2];
        for (int i = 0, until = data.length; i < until; i++) {
            output[(i << 1)] = radix16[data[i] >> 4];
            output[(i << 1) + 1] = radix16[data[i] & 0xf];
        }
        return String.valueOf(output);
    }

    public static String encryptPassword(String password) {
        try {
            byte[] bytes = password.getBytes("utf-8");
            MessageDigest digest = MessageDigest.getInstance("sha-1");
            byte[] result = digest.digest(bytes);
            return asRadix16(result);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new UnexpectedException(e);
        }
    }
}
