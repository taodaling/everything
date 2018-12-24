package com.daltao.script.ast.impl;

import com.daltao.script.ast.Constants;
import com.daltao.utils.FunctionUtils;

import java.lang.reflect.Array;
import java.util.List;
import java.util.function.Function;

public class NativeASTContextImpl extends ASTContextImpl {
    public NativeASTContextImpl() {
        super();

        //add some native methods

        //toString(x) -> return the string form of x
        getProperty("toString").setValue(
                FunctionUtils.newConcatenateFunction()
                        .append(FunctionUtils.fetchFirstOneFunction())
                        .append(Object::toString)
        );

        //toInt(x) -> return the integer form of x
        getProperty("toInt").setValue(
                FunctionUtils.newConcatenateFunction()
                        .append(FunctionUtils.fetchFirstOneFunction())
                        .append(
                                (x -> {
                                    if (x == null) {
                                        return 0;
                                    }
                                    if (x instanceof Integer) {
                                        return x;
                                    }
                                    if (x instanceof String) {
                                        return Integer.parseInt((String) x);
                                    }
                                    throw new UnsupportedOperationException();
                                })));

        //print(x) -> print the argument
        getProperty("print").setValue(
                FunctionUtils.newConcatenateFunction().append(
                        FunctionUtils.fetchFirstOneFunction()
                ).append((x -> {
                    System.out.print(x);
                    return null;
                })));

        //println(x) -> print the argument with new line
        getProperty("println").setValue(
                FunctionUtils.newConcatenateFunction().append(
                        FunctionUtils.fetchFirstOneFunction()
                ).append(x -> {
                    System.out.println(x);
                    return null;
                }));

        //currentTime() -> the milliseconds elapsed since epoch
        getProperty("currentTime").setValue(
                FunctionUtils.newConcatenateFunction().append(x -> System.currentTimeMillis()));

        //array(i1, i2, i3) -> new a multi-dimensions array
        getProperty("array").setValue((Function<List<Object>, Object>)
                list -> {
                    int[] indexes = new int[list.size()];
                    for (int i = 0, until = indexes.length; i < until; i++) {
                        indexes[i] = (Integer) list.get(i);
                    }
                    return Array.newInstance(Object.class, indexes);
                }
        );
    }
}
