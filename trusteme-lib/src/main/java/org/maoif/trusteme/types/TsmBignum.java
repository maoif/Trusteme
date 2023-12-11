package org.maoif.trusteme.types;

import java.math.BigInteger;

public class TsmBignum extends TsmExpr {
    public final BigInteger value;

    public TsmBignum(BigInteger val) {
        this.value = val;
    }
}
