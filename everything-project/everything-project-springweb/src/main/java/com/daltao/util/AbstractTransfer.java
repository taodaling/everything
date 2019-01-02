package com.daltao.util;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractTransfer<S, D> implements Transfer<S, D> {
    @Override
    public List<D> bulkTransfer(List<S> srcs, Supplier<D> supplier) {
        return srcs.stream().map(x -> transfer(x, supplier))
                .collect(Collectors.toList());
    }

    @Override
    public final D transfer(S src, Supplier<D> supplier) {
        if (src == null) {
            return transferNull(supplier);
        }
        return transferNotNull(src, supplier);
    }

    protected D transferNull(Supplier<D> supplier) {
        return null;
    }

    protected abstract D transferNotNull(S src, Supplier<D> supplier);
}
