package com.daltao.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
    private StringUtils() {
    }

    public static String valueOf(Object s) {
        return s == null ? "" : s.toString();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    public static String concatenate(String sep, List<String> values) {
        if (values.isEmpty()) {
            return "";
        }
        int expectedLength = 0;
        for (String value : values) {
            expectedLength += value.length() + sep.length();
        }
        StringBuilder builder = new StringBuilder(expectedLength);
        for (String value : values) {
            builder.append(value).append(sep);
        }
        builder.setLength(builder.length() - sep.length());
        return builder.toString();
    }

    public static String concatenate(String sep, Object... values) {
        return concatenate(sep, Arrays.stream(values).map(Object::toString).collect(Collectors.toList()));
    }

    public static String repeat(String s, int time) {
        if (time < 0) {
            throw new IllegalArgumentException();
        }
        if (s.isEmpty() || time == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(s.length() * time);
        for (int i = 0; i < time; i++) {
            builder.append(s);
        }
        return builder.toString();
    }

    public static String trim(String s, boolean trimHead, boolean trimTail) {
        int l = 0;
        int r = s.length();
        if (trimHead) {
            for (; l < r && Character.isWhitespace(s.charAt(l)); l++) ;
        }
        if (trimTail) {
            for (; l < r && Character.isWhitespace(s.charAt(r - 1)); r--) ;
        }
        if (l == r) {
            return "";
        }
        return s.substring(l, r);
    }

    public static String defaultString(String s, String defaultStr) {
        return s == null ? defaultStr : s;
    }

    public static String percentageEscapeDecode(String s) {
        PercentageEscapeConsumer consumer = new PercentageEscapeConsumer();
        for (int i = 0, until = s.length(); i < until; i++) {
            consumer.consume(s.charAt(i));
        }
        return consumer.toString();
    }

    public static String percentageEscapeEncode(String s) {
        return s.replace("%", "%%");
    }


    private static class PercentageEscapeConsumer {
        private StringBuilder builder = new StringBuilder();
        private boolean open;

        public void consume(char c) {
            if (open) {
                openConsume(c);
            } else {
                closeConsume(c);
            }
        }

        private void closeConsume(char c) {
            if (c == '%') {
                open = true;
            } else {
                builder.append(c);
            }
        }

        public String toString() {
            return builder.toString();
        }

        private void openConsume(char c) {
            open = false;
            switch (c) {
                case '%':
                    builder.append('%');
                    break;
                case 'n':
                    builder.append('\n');
                    break;
                case 't':
                    builder.append('\t');
                    break;
                case 'r':
                    builder.append('\r');
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }
}

