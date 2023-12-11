package org.maoif;

public class sChar extends sExpr {

    private char value;

    public sChar(String src, int pos, char val, int length) {
        super(src, pos, length);
        this.value = val;
    }

    public sChar(String src) {
        super(src);
    }

    public sChar() {
        super();
    }

    public char get() {
        return this.value;
    }

    public void set(char val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}