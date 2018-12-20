package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.ast.Constants;

public class IfNode implements ASTNode {
    private ASTNode condition;
    private ASTNode block;
    private ASTNode elseBlock;

    public IfNode(ASTList list) {
        condition = list.childAt(1);
        block = list.childAt(2);
        if (list.childAt(3) != null) {
            elseBlock = list.listChildAt(3).childAt(2);
        }
    }

    @Override
    public Object eval(ASTContext context) {
        if (Constants.isTrue(condition)) {
            block.eval(context);
        } else if (elseBlock != null) {
            block.eval(context);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("if %s %s", condition, block)
                + elseBlock == null ? "" : (" else " + elseBlock);
    }
}
