package com.daltao.script.bnf;

import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Data
public abstract class AbstractRule implements Rule {
    private String id;
    private Function<ASTList, ASTNode> function = (Function) Function.identity();


}