package com.daltao.reflection;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/3/17.
 */
public class ReflectionUtils {
    private static Pattern setterPattern = Pattern.compile("(set|is)\\w+");
    private static Pattern getterPattern = Pattern.compile("get\\w+");

    public static String getSetterAttributeName(String setter) {
        if (!setter.startsWith("set")) {
            throw new IllegalArgumentException();
        }
        return getAttributeName(setter, 3);
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

    public static boolean isSetter(Method method) {
        return setterPattern.matcher(method.getName()).matches() && method.getReturnType() == Void.TYPE && method.getParameterCount() == 1;
    }

    public static boolean isGetter(Method method) {
        return getterPattern.matcher(method.getName()).matches() && method.getReturnType() != Void.TYPE && method.getParameterCount() == 0;
    }
}
