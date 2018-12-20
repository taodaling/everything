package com.daltao.script.ast;

import java.util.function.Function;

public class Constants {
    public static final Function<ASTList, ASTNode> FETCH_FIRST_ONE = x -> x.childAt(0);
    public static Integer FALSE = 0;

    public static boolean isTrue(Object value) {
        return !FALSE.equals(value);
    }
}
