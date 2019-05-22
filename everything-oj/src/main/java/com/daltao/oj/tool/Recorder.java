package com.daltao.oj.tool;

public class Recorder {
    private Object last;
    private StringBuilder builder = new StringBuilder();

    public synchronized void record(Object who, char c) {
        if (last != who) {
            builder.append('\n').append(who).append(":\n");
            last = who;
        }
        builder.append(c);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}