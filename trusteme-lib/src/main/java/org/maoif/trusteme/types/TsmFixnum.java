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

    @Override
    public boolean isEq(TsmExpr other) {
        return isEqual(other);
    }

    @Override
    public boolean isEqv(TsmExpr other) {
        return isEqual(other);
    }

    @Override
    public boolean isEqual(TsmExpr other) {
        if (other instanceof TsmFixnum n) {
            return n.value == this.value;
        } else {
            return false;
        }
    }
}
