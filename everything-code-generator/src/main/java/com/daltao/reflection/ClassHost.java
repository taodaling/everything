package com.daltao.reflection;

public interface ClassHost {
    public void accept(ClassVisitor visitor) throws Exception;
}
