package org.maoif.trusteme.types;

public class TsmFlonum extends TsmExpr {
    public final double value;

    public TsmFlonum(double val) {
        this.value = val;
    }

    public double get() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public String write() {
        return String.valueOf(this.value);
    }

    @Override
    public boolean isEq(TsmExpr other) {
        // flonum is boxed, after ChezScheme
        return this == other;
    }

    @Override
    public boolean isEqv(TsmExpr other) {
        return isEqual(other);
    }

    @Override
    public boolean isEqual(TsmExpr other) {
        if (other instanceof TsmFlonum n) {
            return n.value == this.value;
        } else {
            return false;
        }
    }
}
