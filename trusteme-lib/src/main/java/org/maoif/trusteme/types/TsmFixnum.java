package org.maoif.trusteme.types;

public class TsmFixnum extends TsmExpr {
    private final long value;

    public TsmFixnum(long val) {
        this.value = val;
    }

    public long get() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public String write() {
        return String.valueOf(value);
    }
}
