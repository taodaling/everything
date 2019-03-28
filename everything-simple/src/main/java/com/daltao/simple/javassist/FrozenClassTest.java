package com.daltao.simple.javassist;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;

public class FrozenClassTest {
    public static void main(String[] args) throws IOException, CannotCompileException, NotFoundException {
        CtClass cls = ClassPool.getDefault().get(ModifiedClass.class.getCanonicalName());
        cls.toBytecode();
        cls.defrost();
        cls.getDeclaredMethod("sayHello", new CtClass[0]);
        cls.addInterface(ClassPool.getDefault().get(Cloneable.class.getCanonicalName()));
        cls.toBytecode();
    }
}
