package com.daltao.script.token;

public abstract class AbstractLineToken extends AbstractToken implements LineToken {
    private final int lineNum;

    public AbstractLineToken(int lineNum, String value) {
        super(value);
        this.lineNum = lineNum;
    }

    @Override
    public int getLineNum() {
        return lineNum;
    }

    @Override
    public String toString() {
        return "\"" + getText() + "\" at line " + lineNum;
    }
}
