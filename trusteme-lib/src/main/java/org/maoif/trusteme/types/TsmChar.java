package org.maoif.trusteme.types;

public class TsmChar extends TsmExpr {
    private final char value;

    public TsmChar(char val) {
        this.value = val;
    }

    public char get() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public String write() {
        return "#\\" + this.value;
    }

}
