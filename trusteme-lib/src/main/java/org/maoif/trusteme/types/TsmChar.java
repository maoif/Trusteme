package org.maoif.trusteme.types;

public class TsmChar extends TsmExpr {
    public final char value;

    public TsmChar(char val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

}
