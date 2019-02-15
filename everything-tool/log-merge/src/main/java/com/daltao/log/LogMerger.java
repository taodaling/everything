package com.daltao.log;

import com.daltao.collection.AbstractIterator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class LogMerger extends AbstractIterator<String> {
    private PriorityQueue<LogMeta> queue;

    public LogMerger(List<Iterator<String>> iterators, Comparator<String> comparator) {
        this.queue = new PriorityQueue<>(iterators.size(), (a, b) -> comparator.compare(a.getLog(), b.getLog()));
        for (Iterator<String> logIterator : iterators) {
            addLogIterator(logIterator);
        }
    }

    private void addLogIterator(Iterator<String> logIterator) {
        if (!logIterator.hasNext()) {
            return;
        }
        queue.add(new LogMeta(logIterator.next(), logIterator));
    }

    @Data
    @AllArgsConstructor
    private static class LogMeta {
        private String log;
        private Iterator<String> iterator;
    }


    @Override
    protected String next0() {
        if (queue.isEmpty()) {
            return end();
        }
        LogMeta min = queue.remove();
        addLogIterator(min.getIterator());
        return min.getLog();
    }
}
