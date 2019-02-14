package com.daltao.log;

import com.daltao.collection.AbstractIterator;
import com.daltao.exception.UnexpectedException;
import com.daltao.template.KMPAutomaton;

import java.io.IOException;
import java.io.Reader;

public class LogReaderAdapter extends AbstractIterator<Log> {
    private final Reader reader;
    private final KMPAutomaton automaton;
    private StringBuilder builder = new StringBuilder();

    public LogReaderAdapter(Reader reader, KMPAutomaton automaton) {
        this.reader = reader;
        this.automaton = automaton;
    }

    private int read() {
        try {
            return reader.read();
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
    }

    private String readString() {
        builder.setLength(0);
        automaton.beginMatch();
        int c;
        while ((c = read()) != -1) {
            automaton.match((char) c);
            builder.append(c);
            if (automaton.isMatch()) {
                builder.setLength(builder.length() - automaton.length());
                return builder.toString();
            }
        }
        return builder.toString();
    }

    @Override
    protected Log next0() {
        String s = readString();
        if (s.isEmpty()) {
            return end();
        }
        return new Log(s);
    }
}
