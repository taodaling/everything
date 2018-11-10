package com.daltao.common;

public interface RestoreAble {
    Object newSavePoint();
    void commit();
    void rollback(Object savePoint);
}
