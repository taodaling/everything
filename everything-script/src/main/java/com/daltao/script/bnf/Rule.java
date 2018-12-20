package com.daltao.script.bnf;

import com.daltao.collection.PredictableIterator;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.token.Token;

import java.util.function.Function;

public interface Rule {
        String getId();

        void setId(String id);

        void setFunction(Function<ASTList,ASTNode> function);

        ASTNode parse(PredictableIterator<Token> iterator);

        boolean match(PredictableIterator<Token> iterator);
    }