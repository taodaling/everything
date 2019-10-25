package com.daltao.simple;

import java.io.Serializable;

public class CastTest {
    public static void main(String[] args) {
        MyUser myUser = null;
        Object s = myUser;
        Serializable t = (Serializable) s;
    }
}


class MyUser implements Serializable {
    public static int invoke() {
        return 1;
    }
}