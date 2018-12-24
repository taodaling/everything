package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;

public class IndexNode implements ASTNode {
    private ASTNode[] indexes;

    public IndexNode(ASTList list) {
        //index : {"[" expr "]"}
        indexes = new ASTNode[list.numberOfChildren()];
        for (int i = 0, until = indexes.length; i < until; i++) {
            indexes[i] = list.listChildAt(i).childAt(1);
        }
    }
}
