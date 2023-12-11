package org.maoif;

import java.math.BigInteger;

public class sBignum extends sNumber {

    private BigInteger value;

    public sBignum(String src) {
        super(src);
    }

    public sBignum(String src, BigInteger val) {
        super(src);
        this.value = val;
    }

    public sBignum(BigInteger value) {
        super();
    }

    public BigInteger get() {
        return this.value;
    }

    public void set(BigInteger val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}