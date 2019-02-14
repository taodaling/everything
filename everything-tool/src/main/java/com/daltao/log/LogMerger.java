package com.daltao.log;

import com.daltao.collection.AbstractIterator;
import lombok.Data;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class LogMerger extends AbstractIterator<Log> {
    private List<Iterator<Log>> iterators;
    private Function<String, Date> dateExtractor;
    private TreeMap<>

    @Data
    private static class LogMeta {
        private String log;
        private Iterator<Log> iterator;
    }


    @Override
    protected Log next0() {

        return null;
    }
}
