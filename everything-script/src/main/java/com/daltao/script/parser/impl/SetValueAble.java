package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTNode;

public interface SetValueAble extends ASTNode {
    void setValue(ASTContext context, Object value);
}
