package com.daltao.util;

import java.util.List;
import java.util.function.Supplier;

public interface Transfer<S, D> {
    D transfer(S src, Supplier<D> supplier);

    List<D> bulkTransfer(List<S> srcs, Supplier<D> supplier);
}
