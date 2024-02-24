package org.maoif.trusteme.types;

public abstract class TsmHashtable extends TsmExpr {
    public abstract void set(TsmExpr key, TsmExpr value);
    public abstract TsmExpr ref(TsmExpr key);
    public abstract boolean contains(TsmExpr key);
    public abstract int size();

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
        return this == other;
    }
}
