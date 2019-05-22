package com.daltao.oj.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class BindingOutputStream extends OutputStream {
    private BlockingQueue<Integer> queue = new LinkedBlockingDeque<>();
    private BindingInputStream inputStream = new BindingInputStream();
    private final Recorder logging;
    private final String name;

    public BindingOutputStream(Recorder logging, String name) {
        this.logging = logging;
        this.name = name;
    }

    public InputStream getAnotherSide() {
        return inputStream;
    }

    @Override
    public void write(int b) throws IOException {
        queue.add(b);
        if (b != -1) {
            logging.record(this, (char) b);
        }
    }

    private class BindingInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            b[off] = (byte) read();
            int i = off + 1;
            for (int until = off + len; i < until; i++) {
                Integer v = queue.poll();
                if (v == null) {
                    break;
                }
                b[i] = v.byteValue();
            }

            return i - off;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
