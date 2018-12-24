package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTNode;

public class NilNode implements ASTNode {
    @Override
    public Object eval(ASTContext context) {
        return null;
    }

    @Override
    public String toString() {
        return "nil";
    }
}
