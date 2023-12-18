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
}
