package com.daltao.log;


import lombok.Data;

@Data
public class Log {
    private final String data;

    public Log(String data) {
        this.data = data;
    }
}
