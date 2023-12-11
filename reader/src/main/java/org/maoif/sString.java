package org.maoif;

public class sString extends sExpr {

    private String value;

    public sString(String src, int pos, String val, int length) {
        super(src, pos, length);
        this.value = val;
    }

    public sString(String src) {
        super(src);
    }

    public String get() {
        return this.value;
    }

    public void set(String val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}