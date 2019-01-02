package com.daltao.util;

import org.springframework.beans.BeanUtils;

import java.util.function.Supplier;

public class SpringTransfer<S, D> extends AbstractTransfer<S, D> {
    @Override
    protected D transferNotNull(S src, Supplier<D> supplier) {
        D result = supplier.get();
        BeanUtils.copyProperties(src, result);
        return result;
    }
}
