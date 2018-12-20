package com.daltao.script.bnf;

import com.daltao.collection.PredictableIterator;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.token.Token;

import java.util.function.Function;

public abstract class ForwardingRule implements Rule {
    protected abstract Rule delegate();

    @Override
    public String getId() {
        return delegate().getId();
    }

    @Override
    public void setId(String id) {
        delegate().setId(id);
    }

    @Override
    public void setFunction(Function<ASTList,ASTNode> function) {
        delegate().setFunction(function);
    }

    @Override
    public ASTNode parse(PredictableIterator<Token> iterator) {
        return delegate().parse(iterator);
    }

    @Override
    public boolean match(PredictableIterator<Token> iterator) {
        return delegate().match(iterator);
    }

    @Override
    public String toString() {
        return delegate().toString();
    }
}
