package com.daltao.script.token.impl;

import com.daltao.script.token.Token;
import lombok.Getter;

public class TypedToken extends Token {
    private TokenType tokenType;

    public TypedToken(String text, int lineNum, TokenType tokenType) {
        super(text, lineNum);
        this.tokenType = tokenType;
    }

    @Override
    public Object getType() {
        return tokenType;
    }
}
