package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.ast.Constants;

public class WhileNode implements ASTNode {
    private ASTNode condition;
    private ASTNode block;

    public WhileNode(ASTList list) {
        //"while" expr block
        condition = list.childAt(1);
        block = list.childAt(2);
    }

    @Override
    public Object eval(ASTContext context) {
        while (Constants.isTrue(condition.eval(context))) {
            block.eval(context);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("while %s %s", condition, block);
    }
}
