package com.daltao.constant;

import com.daltao.model.Identity;

public enum Gender implements Identity<Integer> {
    MAN(0, "man"), WOMAN(1, "woman"), UNKNOWN(2, "unknown");

    private final Integer id;
    private final String name;

    Gender(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Gender of(Integer id) {
        switch (id) {
            case 0:
                return MAN;
            case 1:
                return WOMAN;
            case 2:
                return UNKNOWN;
        }
        throw new IllegalArgumentException();
    }

    public static Gender of(String name) {
        switch (name) {
            case "man":
                return MAN;
            case "woman":
                return WOMAN;
            case "unknown":
                return UNKNOWN;
        }
        throw new IllegalArgumentException();
    }
}
