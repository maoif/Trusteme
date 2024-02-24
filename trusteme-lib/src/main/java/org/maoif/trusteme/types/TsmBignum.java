package org.maoif.trusteme.types;

import java.math.BigInteger;

public class TsmBignum extends TsmExpr {
    public final BigInteger value;

    public TsmBignum(BigInteger val) {
        this.value = val;
    }

    public BigInteger get() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public String write() {
        return this.value.toString();
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
        if (other instanceof TsmBignum n) {
            return n.get().equals(this.value);
        } else {
            return false;
        }
    }
}
