package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTLeaf;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;

public class StringNode implements ASTNode {
    private String text;

    public StringNode(ASTList list) {
        text = ((ASTLeaf) (list.childAt(0))).getToken().getText();
        text = text.substring(1, text.length() - 1);
    }

    @Override
    public Object eval(ASTContext context) {
        return text;
    }

    @Override
    public String toString() {
        return "\"" + text + "\"";
    }
}
