package com.daltao.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class Input2InputStream extends InputStream {
    Deque<Character> deque = new ArrayDeque<>();
    private final Input input;

    public Input2InputStream(Input input) {
        this.input = input;
    }

    @Override
    public int read() throws IOException {
        while (deque.isEmpty() && input.available()) {
            String s = input.readString();
            for (char c : s.toCharArray()) {
                deque.addLast(c);
            }
            deque.addLast('\n');
        }
        if (deque.isEmpty()) {
            return -1;
        }
        return deque.removeFirst();
    }
}
