package com.daltao.bean;

public class Attribute implements Comparable<Attribute> {
    private String name;
    private Class type;
    private boolean readAble;
    private boolean writeAble;

    public Attribute(String name, Class type, boolean readAble, boolean writeAble) {
        this.name = name;
        this.type = type;
        this.readAble = readAble;
        this.writeAble = writeAble;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public boolean isReadAble() {
        return readAble;
    }

    public boolean isWriteAble() {
        return writeAble;
    }

    @Override
    public int compareTo(Attribute o) {
        return name.compareTo(o.name);
    }
}
