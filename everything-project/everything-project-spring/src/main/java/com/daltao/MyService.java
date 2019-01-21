package com.daltao;

@Answer("default")
public interface MyService {
    @Answer("Hello")
    public String hello();

    public String world();
}
