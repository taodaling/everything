package com.daltao.util;


import com.daltao.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daltao on 2018/3/17.
 */
public class ReflectionUtils {

    public static String getSetterAttributeName(String setter) {
        if (!setter.startsWith("set")) {
            throw new IllegalArgumentException();
        }
        return getAttributeName(setter, 3);
    }

    public static String getSetterName(String attribute) {
        StringBuilder builder = new StringBuilder(3 + attribute.length());
        builder.append("set");
        builder.append(attribute);
        builder.setCharAt(3, Character.toUpperCase(builder.charAt(3)));
        return builder.toString();
    }

    public static String getGetterName(String attribute) {
        StringBuilder builder = new StringBuilder(3 + attribute.length());
        builder.append("get");
        builder.append(attribute);
        builder.setCharAt(3, Character.toUpperCase(builder.charAt(3)));
        return builder.toString();
    }

    private static String getAttributeName(String method, int offset) {
        char[] data = method.toCharArray();
        if (data.length <= offset) {
            return "";
        }
        data[offset] = Character.toLowerCase(data[offset]);
        return String.valueOf(data, offset, data.length - offset);
    }

    public static String getGetterAttributeName(String getter) {
        if (!getter.startsWith("get")) {
            throw new IllegalArgumentException();
        }
        return getAttributeName(getter, 3);
    }

    public static byte[] readClassBytes(String className) throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(className.replace('.', '/') + ".class")) {
            return IOUtils.readAll(is);
        }
    }

    public static Class getCompatibleClass(Class a, Class b) {
        if (a.isAssignableFrom(b)) {
            return a;
        } else if (b.isAssignableFrom(a)) {
            return b;
        }
        throw new IllegalArgumentException();
    }

}
