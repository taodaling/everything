package com.daltao.util;

import org.springframework.transaction.support.TransactionTemplate;

public abstract class TransactionalBasicAction<I, O> extends BasicAction<I, O> {
    @Override
    public final O invoke0(I input) {
        return provideTransactionTemplate().execute(status -> invoke0(input));
    }

    protected abstract TransactionTemplate provideTransactionTemplate();

    public abstract O invoke1(I input);
}
