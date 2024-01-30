package org.maoif.trusteme.types;

public abstract class TsmHashtable extends TsmExpr {
    public abstract void set(TsmExpr key, TsmExpr value);
    public abstract TsmExpr ref(TsmExpr key);
    public abstract boolean contains(TsmExpr key);
    public abstract int size();
}
