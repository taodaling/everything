package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class BlockNode implements ASTNode {
    private List<ASTNode> statements = new ArrayList<>();

    public BlockNode(ASTList list) {
        if (list.listChildAt(1).childAt(0) != null) {
            statements.add(list.childAt(1));
        }
        for (ASTNode child : list.listChildAt(2)) {
            //(";"|EOL)[statement]
            ASTList list1 = (ASTList) child;
            if (list1.listChildAt(1).childAt(0) == null) {
                continue;
            }
            ASTNode statement = list1.listChildAt(1).childAt(0);
            statements.add(statement);
        }
    }

    @Override
    public Object eval(ASTContext context) {
        for (ASTNode statement : statements) {
            statement.eval(context);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{\n");
        for (ASTNode statement : statements) {
            builder.append(statement).append("\n");
        }
        builder.append("}");
        return builder.toString();
    }
}
