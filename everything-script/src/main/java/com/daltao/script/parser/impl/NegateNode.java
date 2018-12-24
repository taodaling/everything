package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;

import java.util.Arrays;
import java.util.List;

public class NegateNode implements ASTNode {
    private ASTNode value;

    public NegateNode(ASTList list) {
        //"-" primary
        this.value = list.childAt(1);
    }

    @Override
    public Object eval(ASTContext context) {
        return -(Integer) value.eval(context);
    }

    @Override
    public String toString() {
        return "-" + value;
    }
}
