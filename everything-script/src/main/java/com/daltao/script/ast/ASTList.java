package com.daltao.script.ast;

import com.daltao.script.token.Token;
import com.daltao.utils.Precondition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ASTList implements ASTNode, Iterable<ASTNode> {
    private List<ASTNode> children;

    public ASTList(List<ASTNode> children) {
        this.children = children;
    }

    public ASTList(ASTNode... children) {
        this(Arrays.asList(children));
    }

    public ASTNode childAt(int i) {
        return children.get(i);
    }

    public ASTList listChildAt(int i) {
        return (ASTList) childAt(i);
    }

    public int numberOfChildren() {
        return children.size();
    }

    public Iterator iterator() {
        return children.iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ASTNode child : children) {
            builder.append(child).append(' ');
        }
        return builder.toString();
    }
}
