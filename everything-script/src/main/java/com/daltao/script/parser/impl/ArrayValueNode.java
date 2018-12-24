package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTNode;

public class ArrayValueNode implements SetValueAble {
    private ASTNode array;
    private ASTNode index;

    public ArrayValueNode(ASTNode array, ASTNode index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public Object eval(ASTContext context) {
        return ((Object[]) array.eval(context))[(Integer) index.eval(context)];
    }

    @Override
    public void setValue(ASTContext context, Object value) {
        ((Object[]) array.eval(context))[(Integer) index.eval(context)] = value;
    }

    @Override
    public String toString() {
        return array + "[" + index + "]";
    }
}
