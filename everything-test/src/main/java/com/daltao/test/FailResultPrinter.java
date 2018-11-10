package com.daltao.test;

import com.daltao.utils.Precondition;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class FailResultPrinter implements Consumer<Input[]> {
    private Appendable appendable;

    public FailResultPrinter(Appendable appendable) {
        this.appendable = appendable;
    }

    @Override
    public void accept(Input[] input) {
        Precondition.equal(3, input.length);

        try {
            appendable.append("Input:\n\n");
            record(input[0]);
            appendable.append("\n\n");

            appendable.append("Expect:\n\n");
            record(input[1]);
            appendable.append("\n\n");

            appendable.append("Actual:\n\n");
            record(input[2]);
            appendable.append("\n\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void record(Input input) throws IOException {
        while (input.available()) {
            appendable.append(Objects.toString(input.read())).append('\n');
        }
    }
}
