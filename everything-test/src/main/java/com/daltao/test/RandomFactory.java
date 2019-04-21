package com.daltao.test;

import com.daltao.common.Factory;
import com.daltao.utils.Precondition;

import java.util.Random;
import java.util.function.Supplier;

public abstract class RandomFactory implements Factory<Input> {
    protected Random random = new Random();

    protected int nextInt(int l, int r) {
        return random.nextInt(r - l + 1) + l;
    }
    protected String nextString(int len) {
        Precondition.ge(len, 0);
        StringBuilder builder = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            builder.append((char) (nextInt('a', 'z')));
        }
        return builder.toString();
    }
    protected double nextDouble()
    {
        return random.nextDouble();
    }

    protected long nextLong(long l, long r)
    {
        return l + (long)(nextDouble() * (r - l + 1));
    }
}