package org.maoif;

public class sBool extends sExpr {

    private boolean value;

    public sBool(String src, int pos, int length, boolean val) {
        super(src, pos, length);
        this.value = val;
    }

    public sBool() {
        super();
    }

    public boolean get() {
        return this.value;
    }

    public void set(boolean val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return value ? "#t" : "#f";
    }
}