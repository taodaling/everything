package com.daltao.message;

import java.util.Objects;

public class PlainMessage extends AbstractMessage {
    private final String msg;

    public PlainMessage(String msg) {
        this.msg = Objects.requireNonNull(msg);
    }

    @Override
    protected String toString0() {
        return msg;
    }
}
