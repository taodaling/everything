package com.daltao.util;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionalActionWrapper<I, O> implements Action<I, O> {
    private Action<I, O> action;
    private TransactionTemplate template;

    public TransactionalActionWrapper(Action<I, O> action, TransactionTemplate template) {
        this.action = action;
        this.template = template;
    }

    @Override
    public O invoke(I input) {
       return template.execute(new TransactionCallback<O>() {
           @Override
           public O doInTransaction(TransactionStatus status) {
               return action.invoke(input);
           }
       });
    }

    @Override
    public Action<I, O> addListener(ActionListener<I, O> listener) {
        return action.addListener(listener);
    }

    @Override
    public Action<I, O> removeListener(ActionListener<I, O> listener) {
        return action.addListener(listener);
    }
}
