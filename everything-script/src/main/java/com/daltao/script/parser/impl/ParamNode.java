package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ParamNode implements ASTNode {
    private List<ASTNode> paramList = new ArrayList<>();

    public ParamNode(ASTList list) {
//param-def : [identifier {"," identifier}]
        if (list.childAt(0) == null) {
            return;
        }
        list = list.listChildAt(0);

        paramList.add(list.childAt(0));
        for (ASTNode follow : list.listChildAt(1)) {
            ASTNode node = ((ASTList) follow).childAt(1);
            paramList.add(node);
        }
    }

    public List<ASTNode> getParamList() {
        return paramList;
    }

    @Override
    public String toString() {
        return StringUtils.concatenate(",", paramList.toArray());
    }
}
