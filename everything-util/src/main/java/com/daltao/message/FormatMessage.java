package com.daltao.message;

import java.text.MessageFormat;

public class FormatMessage extends AbstractMessage {
    private final String pattern;
    private final Object[] args;

    public FormatMessage(String pattern, Object... args) {
        this.pattern = pattern;
        this.args = args;
    }

    @Override
    protected String toString0() {
        return MessageFormat.format(pattern, args);
    }
}
