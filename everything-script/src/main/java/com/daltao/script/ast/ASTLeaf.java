package com.daltao.script.ast;

import com.daltao.script.token.Token;

public class ASTLeaf implements ASTNode {
    private Token token;

    public ASTLeaf(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public static ASTNode newInstance(Token token) {
        return new ASTLeaf(token);
    }

    @Override
    public String toString() {
        return token.getText() + " ";
    }

    @Override
    public Object eval(ASTContext context) {
        return null;
    }
}
