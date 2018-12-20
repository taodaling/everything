package com.daltao.script.ast;

public interface ASTNode {
    default Object eval(ASTContext context) {
        throw new UnsupportedOperationException();
    }
}
