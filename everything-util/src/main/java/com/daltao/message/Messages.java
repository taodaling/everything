package com.daltao.message;

public class Messages {
    private Messages() {
    }

    public Message newPlainMessage(String msg) {
        return new PlainMessage(msg);
    }

    public static Message newFormatMessage(String pattern, Object... args) {
        return new FormatMessage(pattern, args);
    }
}
