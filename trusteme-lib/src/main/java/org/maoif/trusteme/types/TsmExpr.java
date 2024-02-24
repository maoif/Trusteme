package org.maoif.trusteme.types;

import com.oracle.truffle.api.interop.TruffleObject;

/**
 * Parent of all Trusteme types.
 */
public abstract class TsmExpr implements TruffleObject {
    // used to write the datum's textual representation
    public abstract String write();

    public abstract boolean isEq(TsmExpr other);
    public abstract boolean isEqv(TsmExpr other);
    public abstract boolean isEqual(TsmExpr other);
}
