package com.daltao.message;

public abstract class AbstractMessage implements Message {
    private String cache;

    @Override
    public final String toString() {
        if (cache == null) {
            cache = toString0();
            if (cache == null) {
                throw new IllegalStateException();
            }
        }
        return cache;
    }

    protected abstract String toString0();

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass().equals(obj.getClass()) && toString().equals(obj.toString());
    }
}
