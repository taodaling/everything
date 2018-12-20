package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTLeaf;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;

public class LiteralNumberNode implements ASTNode {
    private Integer value;

    public LiteralNumberNode(ASTList list) {
        ASTLeaf leaf = (ASTLeaf) list.childAt(0);
        value = Integer.parseInt(leaf.getToken().getText());
    }

    @Override
    public Object eval(ASTContext context) {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
