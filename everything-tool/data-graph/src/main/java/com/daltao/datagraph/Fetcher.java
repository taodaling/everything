package com.daltao.datagraph;

import java.util.Collection;
import java.util.Map;

public interface Fetcher<I, O> {
    Map<I, O> get(Collection<I> keys);
}
