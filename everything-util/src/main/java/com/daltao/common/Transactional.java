package com.daltao.common;

public interface Transactional {
    Object savePoint();

    void rollback(Object savePoint);

    void commit(Object savePoint);
}
