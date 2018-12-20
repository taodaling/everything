package com.daltao.script.token;

import lombok.Getter;

@Getter
public class Token {
    private String text;
    private int lineNum;

    public Token(String text, int lineNum) {
        this.text = text;
        this.lineNum = lineNum;
    }


    @Override
    public String toString() {
        return text;
    }

    public Object getType() {
        return getClass();
    }
}
